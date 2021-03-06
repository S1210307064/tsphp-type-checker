/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.LowerCaseStringMap;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tsphp.typechecker.utils.MapHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class NamespaceScope implements INamespaceScope
{

    private final ILowerCaseStringMap<List<IAliasSymbol>> usesCaseInsensitive = new LowerCaseStringMap<>();
    private final Map<String, List<IAliasSymbol>> uses = new LinkedHashMap<>();
    private final IScopeHelper scopeHelper;
    private final String scopeName;
    private final IGlobalNamespaceScope globalNamespaceScope;
    private final ITypeCheckerErrorReporter typeCheckerErrorReporter;

    public NamespaceScope(
            IScopeHelper theScopeHelper,
            String theScopeName,
            IGlobalNamespaceScope theGlobalNamespaceScope,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {

        scopeHelper = theScopeHelper;
        scopeName = theScopeName;
        globalNamespaceScope = theGlobalNamespaceScope;
        typeCheckerErrorReporter = theTypeCheckerErrorReporter;
    }

    @Override
    public String getScopeName() {
        return scopeName;
    }

    @Override
    public IScope getEnclosingScope() {
        return globalNamespaceScope;
    }

    @Override
    public void define(ISymbol symbol) {
        //we define symbols in the corresponding global namespace scope in order that it can be found from other
        //namespaces as well
        globalNamespaceScope.define(symbol);
        //However, definition scope is this one, is used for alias resolving and name clashes
        symbol.setDefinitionScope(this);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        //check in global namespace scope because they have been defined there
        return globalNamespaceScope.doubleDefinitionCheck(symbol);
    }

    @Override
    public boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol) {
        //check in global namespace scope because they have been defined there
        return globalNamespaceScope.doubleDefinitionCheckCaseInsensitive(symbol);
    }

    @Override
    public void defineUse(IAliasSymbol symbol) {
        MapHelper.addToListMap(usesCaseInsensitive, symbol.getName(), symbol);
        MapHelper.addToListMap(uses, symbol.getName(), symbol);
        symbol.setDefinitionScope(this);
    }

    @Override
    public boolean useDefinitionCheck(IAliasSymbol symbol) {
        boolean isNotDoubleDefined = scopeHelper.checkIsNotDoubleDefinition(
                usesCaseInsensitive.get(symbol.getName()).get(0), symbol);
        return isNotDoubleDefined && isNotAlreadyDefinedAsType(symbol);

    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private boolean isNotAlreadyDefinedAsType(IAliasSymbol symbol) {
        ITypeSymbol typeSymbol = globalNamespaceScope.getTypeSymbolWhichClashesWithUse(symbol.getDefinitionAst());
        boolean ok = hasNoTypeNameClash(symbol.getDefinitionAst(), typeSymbol);
        if (!ok) {
            typeCheckerErrorReporter.determineAlreadyDefined(symbol, typeSymbol);
        }
        return ok;
    }

    private boolean hasNoTypeNameClash(ITSPHPAst useDefinition, ITypeSymbol typeSymbol) {
        boolean hasNoTypeNameClash = typeSymbol == null;
        if (!hasNoTypeNameClash) {
            boolean isUseDefinedEarlier = useDefinition.isDefinedEarlierThan(typeSymbol.getDefinitionAst());
            boolean isUseInDifferentNamespaceStatement = typeSymbol.getDefinitionScope() != this;

            //There is no type name clash in the following situation: namespace{use a as b;} namespace{ class b{}}
            //because: use is defined earlier and the use statement is in a different namespace statement
            hasNoTypeNameClash = isUseDefinedEarlier && isUseInDifferentNamespaceStatement;
        }
        return hasNoTypeNameClash;

    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        //we resolve from the corresponding global namespace scope 
        return globalNamespaceScope.resolve(ast);
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        return unsafeCast(uses);
    }


    @Override
    public void addToInitialisedSymbols(ISymbol symbol, boolean isFullyInitialised) {
        globalNamespaceScope.addToInitialisedSymbols(symbol, isFullyInitialised);
    }

    @Override
    public Map<String, Boolean> getInitialisedSymbols() {
        return globalNamespaceScope.getInitialisedSymbols();
    }

    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        return globalNamespaceScope.isFullyInitialised(symbol);
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        return globalNamespaceScope.isPartiallyInitialised(symbol);
    }

    @SuppressWarnings("unchecked")
    private <TCastTo> TCastTo unsafeCast(Object o) {
        return (TCastTo) o;
    }

    @Override
    public List<IAliasSymbol> getUse(String alias) {
        return uses.get(alias);
    }

    @Override
    public ITSPHPAst getCaseInsensitiveFirstUseDefinitionAst(String alias) {
        return usesCaseInsensitive.containsKey(alias)
                ? usesCaseInsensitive.get(alias).get(0).getDefinitionAst()
                : null;
    }

}
