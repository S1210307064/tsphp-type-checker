/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.LowerCaseStringMap;
import ch.tsphp.common.exceptions.DefinitionException;
import ch.tsphp.common.exceptions.ReferenceException;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.IVariableSymbol;

public class SymbolResolver implements ISymbolResolver
{

    private final IScopeHelper scopeHelper;
    private final ISymbolFactory symbolFactory;
    private final ITypeCheckerErrorReporter typeCheckErrorReporter;

    private ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes = new LowerCaseStringMap<>();
    private final IGlobalNamespaceScope globalDefaultNamespace;


    public SymbolResolver(
            IScopeHelper theScopeHelper,
            ISymbolFactory theSymbolFactory,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ILowerCaseStringMap<IGlobalNamespaceScope> theGlobalNamespaceScopes,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {
        scopeHelper = theScopeHelper;
        symbolFactory = theSymbolFactory;
        typeCheckErrorReporter = theTypeCheckerErrorReporter;
        globalNamespaceScopes = theGlobalNamespaceScopes;
        globalDefaultNamespace = theGlobalDefaultNamespace;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        //Alias is always pointing to a full type name. If user has omitted \ at the beginning, then we add it here
        String typeName = typeAst.getText();
        if (!isAbsolute(typeName)) {
            typeName = "\\" + typeName;
            typeAst.setText(typeName);
        }

        return (ITypeSymbol) resolveGlobalIdentifier(typeAst);
    }

    @Override
    public boolean isAbsolute(String typeName) {
        return typeName.substring(0, 1).equals("\\");
    }

    @Override
    public ISymbol resolveGlobalIdentifierWithFallback(ITSPHPAst ast) {
        ISymbol symbol = resolveGlobalIdentifier(ast);
        symbol = fallbackIfNull(symbol, ast);
        return symbol;
    }

    private ISymbol fallbackIfNull(ISymbol symbol, ITSPHPAst ast) {
        ISymbol resolvedSymbol = symbol;
        if (symbol == null) {
            resolvedSymbol = globalDefaultNamespace.resolve(ast);
        }
        return resolvedSymbol;
    }

    @Override
    public ISymbol resolveGlobalIdentifier(ITSPHPAst typeAst) {
        ISymbol symbol;

        if (isAbsolute(typeAst.getText())) {
            symbol = resolveAbsoluteIdentifier(typeAst);
        } else {
            symbol = resolveIdentifierCompriseAlias(typeAst);

            if (symbol == null && isRelative(typeAst.getText())) {
                symbol = resolveRelativeIdentifier(typeAst);
            }
        }
        return symbol;
    }

    private ISymbol resolveAbsoluteIdentifier(ITSPHPAst typeAst) {
        ISymbol symbol = null;
        IGlobalNamespaceScope scope = scopeHelper
                .getCorrespondingGlobalNamespace(globalNamespaceScopes, typeAst.getText());
        if (scope != null) {
            symbol = scope.resolve(typeAst);
        }
        return symbol;
    }

    private ISymbol resolveIdentifierCompriseAlias(ITSPHPAst typeAst) {
        INamespaceScope scope = scopeHelper.getEnclosingNamespaceScope(typeAst);
        ISymbol symbol = scope.resolve(typeAst);

        String alias = getPotentialAlias(typeAst.getText());
        ITSPHPAst useDefinition = scope.getCaseInsensitiveFirstUseDefinitionAst(alias);
        useDefinition = checkTypeNameClashAndRecoverIfNecessary(useDefinition, symbol);

        if (useDefinition != null) {
            symbol = resolveAlias(useDefinition, alias, typeAst);
        }
        return symbol;
    }

    private String getPotentialAlias(String typeName) {
        String alias = typeName;
        int backslashPosition = typeName.indexOf("\\");
        if (backslashPosition != -1) {
            alias = typeName.substring(0, backslashPosition);
        }
        return alias;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private ITSPHPAst checkTypeNameClashAndRecoverIfNecessary(ITSPHPAst useDefinition, ISymbol typeSymbol) {
        ITSPHPAst resultingUseDefinition = useDefinition;
        if (hasTypeNameClash(useDefinition, typeSymbol)) {
            ITSPHPAst typeDefinition = typeSymbol.getDefinitionAst();
            if (useDefinition.isDefinedEarlierThan(typeDefinition)) {
                typeCheckErrorReporter.alreadyDefined(useDefinition, typeDefinition);
            } else {
                typeCheckErrorReporter.alreadyDefined(typeDefinition, useDefinition);
                //we do not use the alias if it was defined later than the typeSymbol
                resultingUseDefinition = null;
            }
        }
        return resultingUseDefinition;
    }

    private boolean hasTypeNameClash(ITSPHPAst useDefinition, ISymbol symbol) {
        return useDefinition != null && symbol != null && symbol.getDefinitionScope().equals(this);
    }

    private ISymbol resolveAlias(ITSPHPAst useDefinition, String alias, ITSPHPAst typeAst) {
        ISymbol symbol;

        if (useDefinition.isDefinedEarlierThan(typeAst)) {
            symbol = useDefinition.getSymbol().getType();
            String typeName = typeAst.getText();
            if (isUsedAsNamespace(alias, typeName)) {
                typeName = getAbsoluteTypeName((ITypeSymbol) symbol, alias, typeName);
                typeAst.setText(typeName);

                IGlobalNamespaceScope globalNamespaceScope = scopeHelper
                        .getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);
                if (globalNamespaceScope != null) {
                    symbol = globalNamespaceScope.resolve(typeAst);
                } else {
                    ReferenceException ex = typeCheckErrorReporter.notDefined(typeAst);
                    symbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);
                }
            }
        } else {
            DefinitionException ex = typeCheckErrorReporter.aliasForwardReference(typeAst, useDefinition);
            symbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);
        }
        return symbol;
    }

    private String getAbsoluteTypeName(ITypeSymbol typeSymbol, String alias, String typeName) {
        String fullTypeName;
        //alias does not point to a real type
        if (typeSymbol instanceof IAliasTypeSymbol) {
            fullTypeName = typeSymbol.getName() + "\\";
        } else {
            fullTypeName = typeSymbol.getDefinitionScope().getScopeName() + typeSymbol.getName() + "\\";
        }
        if (!fullTypeName.substring(0, 1).equals("\\")) {
            fullTypeName = "\\" + fullTypeName;
        }
        return fullTypeName + typeName.substring(alias.length() + 1);
    }

    private boolean isUsedAsNamespace(String alias, String typeName) {
        return !alias.equals(typeName);
    }

    @Override
    public IScope getEnclosingGlobalNamespaceScope(IScope scope) {
        IScope globalNamespaceScope = scope;
        IScope tmp = scope.getEnclosingScope();
        while (tmp != null) {
            globalNamespaceScope = tmp;
            tmp = tmp.getEnclosingScope();
        }
        return globalNamespaceScope;
    }

    private boolean isRelative(String identifier) {
        return identifier.indexOf("\\") > 0;
    }

    private ISymbol resolveRelativeIdentifier(ITSPHPAst typeAst) {
        IScope enclosingGlobalNamespaceScope = getEnclosingGlobalNamespaceScope(typeAst.getScope());
        String typeName = enclosingGlobalNamespaceScope.getScopeName() + typeAst.getText();
        typeAst.setText(typeName);
        IGlobalNamespaceScope scope = scopeHelper.getCorrespondingGlobalNamespace(globalNamespaceScopes, typeName);

        ISymbol typeSymbol = null;
        if (scope != null) {
            typeSymbol = scope.resolve(typeAst);
        }
        return typeSymbol;
    }

    @Override
    public IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = null;
        IScope scope = ast.getScope();
        while (scope != null && !(scope instanceof IClassTypeSymbol)) {
            scope = scope.getEnclosingScope();
        }
        if (scope != null) {
            classTypeSymbol = (IClassTypeSymbol) scope;
        }
        return classTypeSymbol;
    }

    @Override
    public IMethodSymbol getEnclosingMethod(ITSPHPAst ast) {
        IMethodSymbol methodSymbol = null;
        IScope scope = ast.getScope();
        while (scope != null && !(scope instanceof IMethodSymbol)) {
            scope = scope.getEnclosingScope();
        }
        if (scope != null) {
            methodSymbol = (IMethodSymbol) scope;
        }
        return methodSymbol;
    }

    @Override
    public IVariableSymbol resolveConstant(ITSPHPAst ast) {
        ISymbol symbol;
        if (ast.getText().contains("\\")) {
            symbol = resolveGlobalIdentifierWithFallback(ast);
        } else {
            INamespaceScope scope = scopeHelper.getEnclosingNamespaceScope(ast);
            symbol = scope.resolve(ast);
            symbol = fallbackIfNull(symbol, ast);
        }
        return (IVariableSymbol) symbol;
    }
}
