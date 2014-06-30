/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITSPHPErrorAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.exceptions.ReferenceException;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.exceptions.TypeCheckerException;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IConditionalScope;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousVariableSymbol;
import org.antlr.runtime.RecognitionException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReferencePhaseController implements IReferencePhaseController
{
    private final ISymbolFactory symbolFactory;
    private final ISymbolResolver symbolResolver;
    private final ITypeCheckerErrorReporter typeCheckErrorReporter;

    private final IGlobalNamespaceScope globalDefaultNamespace;

    public ReferencePhaseController(
            ISymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {
        symbolFactory = theSymbolFactory;
        symbolResolver = theSymbolResolver;
        typeCheckErrorReporter = theTypeCheckerErrorReporter;
        globalDefaultNamespace = theGlobalDefaultNamespace;

    }

    @Override
    public IVariableSymbol resolveConstant(ITSPHPAst ast) {
        IVariableSymbol symbol = symbolResolver.resolveConstant(ast);

        if (symbol == null) {
            ReferenceException exception = typeCheckErrorReporter.notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return symbol;
    }

    @Override
    public IVariableSymbol resolveThisSelf(ITSPHPAst ast) {
        return resolveThis(getEnclosingClass(ast), ast);
    }

    private IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = symbolResolver.getEnclosingClass(ast);
        if (classTypeSymbol == null) {
            ReferenceException ex = typeCheckErrorReporter.notInClass(ast);
            classTypeSymbol = symbolFactory.createErroneousTypeSymbol(ast, ex);
        }
        return classTypeSymbol;
    }

    private IVariableSymbol resolveThis(IClassTypeSymbol classTypeSymbol, ITSPHPAst $this) {
        IVariableSymbol variableSymbol;
        if (classTypeSymbol != null) {
            variableSymbol = classTypeSymbol.getThis();
            if (variableSymbol == null) {
                variableSymbol = symbolFactory.createThisSymbol($this, classTypeSymbol);
                classTypeSymbol.setThis(variableSymbol);
            }
        } else {
            ReferenceException exception = typeCheckErrorReporter.notInClass($this);
            variableSymbol = symbolFactory.createErroneousVariableSymbol($this, exception);
        }
        return variableSymbol;
    }

    @Override
    public IVariableSymbol resolveParent(ITSPHPAst ast) {
        return resolveThis(getParent(ast), ast);
    }

    private IClassTypeSymbol getParent(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        IClassTypeSymbol parent = classTypeSymbol.getParent();
        if (parent == null) {
            TypeCheckerException ex = typeCheckErrorReporter.noParentClass(ast);
            parent = symbolFactory.createErroneousTypeSymbol(ast, ex);
        }
        return parent;
    }

    @Override
    public IVariableSymbol resolveVariable(ITSPHPAst ast) {
        ISymbol symbol = ast.getScope().resolve(ast);
        if (symbol == null) {
            ReferenceException exception = typeCheckErrorReporter.notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public IScalarTypeSymbol resolveScalarType(ITSPHPAst typeAst, boolean isNullable) {
        String typeName = typeAst.getText();
        if (isNullable) {
            typeAst.setText(typeName + "?");
        }
        IScalarTypeSymbol typeSymbol = (IScalarTypeSymbol) resolvePrimitiveType(typeAst);
        if (isNullable) {
            typeAst.setText(typeName);
        }
        return typeSymbol;
    }

    @Override
    public ITypeSymbol resolvePrimitiveType(ITSPHPAst typeAst) {
        ITypeSymbol typeSymbol = (ITypeSymbol) globalDefaultNamespace.resolve(typeAst);
        if (typeSymbol == null) {
            rewriteNameToAbsoluteType(typeAst);
            ReferenceException ex = typeCheckErrorReporter.unknownType(typeAst);
            typeSymbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);

        }
        return typeSymbol;
    }

    /**
     * Return the absolute name of a type which could not be found (prefix the enclosing namespace).
     */
    private void rewriteNameToAbsoluteType(ITSPHPAst typeAst) {
        String typeName = typeAst.getText();
        if (!symbolResolver.isAbsolute(typeName)) {
            String namespace = symbolResolver.getEnclosingGlobalNamespaceScope(typeAst.getScope()).getScopeName();
            typeAst.setText(namespace + typeName);
        }
    }

    @Override
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        ITypeSymbol symbol = (ITypeSymbol) symbolResolver.resolveGlobalIdentifier(typeAst);

        if (symbol == null) {
            rewriteNameToAbsoluteType(typeAst);
            ReferenceException ex = typeCheckErrorReporter.unknownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);

        } else if (symbol instanceof IAliasTypeSymbol) {

            typeAst.setText(symbol.getName());
            ReferenceException ex = typeCheckErrorReporter.unknownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(symbol.getDefinitionAst(), ex);
        }
        return symbol;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        ITypeSymbol aliasTypeSymbol = symbolResolver.resolveUseType(typeAst, alias);
        if (aliasTypeSymbol == null) {
            aliasTypeSymbol = symbolFactory.createAliasTypeSymbol(typeAst, typeAst.getText());
        }
        return aliasTypeSymbol;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public boolean checkIsInterface(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isInterface = symbol instanceof IInterfaceTypeSymbol;
        if (!isInterface) {
            typeCheckErrorReporter.interfaceExpected(typeAst);
        }
        return isInterface;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public boolean checkIsClass(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isClass = symbol instanceof IClassTypeSymbol;
        if (!isClass) {
            typeCheckErrorReporter.classExpected(typeAst);
        }
        return isClass;
    }

    @Override
    public boolean checkVariableIsOkToUse(ITSPHPAst variableId) {
        boolean isNotForwardReference = checkIsNotForwardReference(variableId);
        boolean isNotOutOfScope = checkIsNotOutOfConditionalScope(variableId);
        return isNotForwardReference
                && isNotOutOfScope
                && checkVariableIsInitialised(variableId);
    }

    @Override
    public boolean checkIsNotForwardReference(ITSPHPAst ast) {
        ISymbol symbol = ast.getSymbol();
        boolean isNotUsedBefore = true;
        //only check if not already an error occurred in conjunction with this ast (for instance missing declaration)
        if (!(symbol instanceof IErroneousSymbol)) {
            ITSPHPAst definitionAst = symbol.getDefinitionAst();
            isNotUsedBefore = definitionAst.isDefinedEarlierThan(ast);
            if (!isNotUsedBefore) {
                typeCheckErrorReporter.forwardReference(ast, definitionAst);
            }
        }
        return isNotUsedBefore;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public boolean checkIsNotOutOfConditionalScope(ITSPHPAst ast) {
        boolean ok = true;
        ISymbol symbol = ast.getSymbol();
        if (symbol.getDefinitionScope() instanceof IConditionalScope) {
            IScope currentScope = ast.getScope();
            if (!(currentScope instanceof IConditionalScope)) {
                ok = false;
                typeCheckErrorReporter.variableDefinedInConditionalScope(
                        ast.getSymbol().getDefinitionAst(), ast);
            } else if (isNotDefinedInThisNorOuterScope(symbol, currentScope)) {
                ok = false;
                typeCheckErrorReporter.variableDefinedInOtherConditionalScope(
                        symbol.getDefinitionAst(), ast);
            }
        }
        return ok;
    }

    private boolean isNotDefinedInThisNorOuterScope(ISymbol symbol, IScope scope) {
        boolean isNotDefinedInThisNorOuterScope = true;
        IScope definitionScope = symbol.getDefinitionScope();
        while (scope != null && scope instanceof IConditionalScope) {
            if (scope == definitionScope) {
                isNotDefinedInThisNorOuterScope = false;
                break;
            }
            scope = scope.getEnclosingScope();
        }
        return isNotDefinedInThisNorOuterScope;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public boolean checkVariableIsInitialised(ITSPHPAst variableId) {
        IScope scope = variableId.getScope();
        ISymbol symbol = variableId.getSymbol();
        if (!(symbol instanceof IErroneousVariableSymbol)) {
            if (!scope.isFullyInitialised(symbol) && isNotLeftHandSideOfAssignment(variableId)) {
                ITypeCheckerErrorReporter errorReporter = typeCheckErrorReporter;
                if (scope.isPartiallyInitialised(symbol)) {
                    errorReporter.variablePartiallyInitialised(symbol.getDefinitionAst(), variableId);
                } else {
                    errorReporter.variableNotInitialised(symbol.getDefinitionAst(), variableId);
                }
                return false;
            }
        }
        return true;
    }

    @Override
    public void sendUpInitialisedSymbols(ITSPHPAst blockConditional) {
        IScope scope = blockConditional.getScope();
        Map<String, Boolean> enclosingInitialisedSymbols = scope.getEnclosingScope().getInitialisedSymbols();
        for (Map.Entry<String, Boolean> entry : scope.getInitialisedSymbols().entrySet()) {
            String symbolName = entry.getKey();
            if (!enclosingInitialisedSymbols.containsKey(symbolName)) {
                enclosingInitialisedSymbols.put(symbolName, false);
            }
        }
    }


    @Override
    public void sendUpInitialisedSymbolsAfterIf(ITSPHPAst ifBlock, ITSPHPAst elseBlock) {
        if (elseBlock != null) {
            List<ITSPHPAst> conditionalBlocks = new ArrayList<>();
            conditionalBlocks.add(ifBlock);
            conditionalBlocks.add(elseBlock);
            sendUpInitialisedSymbolsAfterTryCatch(conditionalBlocks);
        } else {
            IScope scope = ifBlock.getScope();
            Map<String, Boolean> enclosingInitialisedSymbols = scope.getEnclosingScope().getInitialisedSymbols();
            for (String symbolName : scope.getInitialisedSymbols().keySet()) {
                if (!enclosingInitialisedSymbols.containsKey(symbolName)) {
                    enclosingInitialisedSymbols.put(symbolName, false);
                }
            }
        }
    }

    @Override
    public void sendUpInitialisedSymbolsAfterSwitch(List<ITSPHPAst> conditionalBlocks, boolean hasDefaultLabel) {
        if (hasDefaultLabel) {
            sendUpInitialisedSymbolsAfterTryCatch(conditionalBlocks);
        } else {
            Map<String, Boolean> enclosingInitialisedSymbols =
                    conditionalBlocks.get(0).getScope().getEnclosingScope().getInitialisedSymbols();
            for (ITSPHPAst block : conditionalBlocks) {
                for (String symbolName : block.getScope().getInitialisedSymbols().keySet()) {
                    if (!enclosingInitialisedSymbols.containsKey(symbolName)) {
                        //without default label they are only partially initialised
                        enclosingInitialisedSymbols.put(symbolName, false);
                    }
                }
            }
        }
    }

    @Override
    public void sendUpInitialisedSymbolsAfterTryCatch(List<ITSPHPAst> conditionalBlocks) {
        if (conditionalBlocks.size() > 0) {
            Set<String> allKeys = new HashSet<>();
            Set<String> commonKeys = new HashSet<>();
            boolean isFirst = true;
            for (ITSPHPAst block : conditionalBlocks) {
                Set<String> keys = block.getScope().getInitialisedSymbols().keySet();
                allKeys.addAll(keys);
                if (!isFirst) {
                    commonKeys.retainAll(keys);
                } else {
                    commonKeys.addAll(keys);
                    isFirst = false;
                }
            }

            Map<String, Boolean> enclosingInitialisedSymbols =
                    conditionalBlocks.get(0).getScope().getEnclosingScope().getInitialisedSymbols();

            for (String symbolName : allKeys) {
                if (!enclosingInitialisedSymbols.containsKey(symbolName)
                        || !enclosingInitialisedSymbols.get(symbolName)) {

                    boolean isFullyInitialised = commonKeys.contains(symbolName);
                    if (isFullyInitialised) {
                        for (ITSPHPAst block : conditionalBlocks) {
                            if (!block.getScope().getInitialisedSymbols().get(symbolName)) {
                                isFullyInitialised = false;
                                break;
                            }
                        }
                    }
                    enclosingInitialisedSymbols.put(symbolName, isFullyInitialised);
                }
            }
        }
    }

    private boolean isNotLeftHandSideOfAssignment(ITSPHPAst variableId) {
        ITSPHPAst parent = (ITSPHPAst) variableId.getParent();
        int type = parent.getType();
        return type != TSPHPDefinitionWalker.Assign && type != TSPHPDefinitionWalker.CAST_ASSIGN
                || !parent.getChild(0).equals(variableId);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkReturnsFromFunction(boolean isReturning, boolean hasAtLeastOneReturnOrThrow,
            ITSPHPAst identifier) {
        if (!isReturning) {
            if (hasAtLeastOneReturnOrThrow) {
                typeCheckErrorReporter.partialReturnFromFunction(identifier);
            } else {
                typeCheckErrorReporter.noReturnFromFunction(identifier);
            }
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkReturnsFromMethod(boolean isReturning, boolean hasAtLeastOneReturnOrThrow, ITSPHPAst identifier) {
        if (!isReturning) {
            if (hasAtLeastOneReturnOrThrow) {
                typeCheckErrorReporter.partialReturnFromMethod(identifier);
            } else {
                typeCheckErrorReporter.noReturnFromMethod(identifier);
            }
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Override
    public void checkBreakContinueLevel(ITSPHPAst root, ITSPHPAst level) {
        int levels = level == null ? 1 : Integer.parseInt(level.getText());
        int count = 0;

        if (levels > 0) {
            ITSPHPAst parent = (ITSPHPAst) root.getParent();
            while (parent != null) {
                if (isLoop(parent.getType())) {
                    ++count;
                }
                parent = (ITSPHPAst) parent.getParent();
            }
            if (count < levels) {
                typeCheckErrorReporter.toManyBreakContinueLevels(root);
            }
        } else {
            typeCheckErrorReporter.breakContinueLevelZeroNotAllowed(root);
        }
    }

    private boolean isLoop(int type) {
        //CHECKSTYLE:OFF:BooleanExpressionComplexity
        return type == TSPHPDefinitionWalker.Switch
                || type == TSPHPDefinitionWalker.For
                || type == TSPHPDefinitionWalker.Foreach
                || type == TSPHPDefinitionWalker.Do
                || type == TSPHPDefinitionWalker.While;
        //CHECKSTYLE:ON:BooleanExpressionComplexity
    }

    @Override
    public IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPErrorAst erroneousTypeAst) {
        return symbolFactory.createErroneousTypeSymbol(erroneousTypeAst, erroneousTypeAst.getException());
    }

    @Override
    public IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPAst typeAst, RecognitionException ex) {
        return symbolFactory.createErroneousTypeSymbol(typeAst, new TSPHPException(ex));
    }
}
