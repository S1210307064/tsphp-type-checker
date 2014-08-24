/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITSPHPErrorAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import org.antlr.runtime.RecognitionException;

import java.util.List;

/**
 * Represents the interface between the TSPHPReferenceWalker (ANTLR generated) and the logic.
 */
public interface IReferencePhaseController
{
    IVariableSymbol resolveConstant(ITSPHPAst ast);

    IVariableSymbol resolveThisSelf(ITSPHPAst $this);

    IVariableSymbol resolveParent(ITSPHPAst $this);

    IVariableSymbol resolveVariable(ITSPHPAst ast);

    IScalarTypeSymbol resolveScalarType(ITSPHPAst typeAst, boolean isNullable);

    ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt);

    /**
     * Try to resolve the type for the given typeAst and returns an
     * {@link IErroneousTypeSymbol} if the type could not be found.
     *
     * @param typeAst The AST node which contains the type name. For instance, int, MyClass, \Exception etc.
     * @return The corresponding type or a {@link IErroneousTypeSymbol}
     * if could not be found.
     */
    ITypeSymbol resolveType(ITSPHPAst typeAst);

    ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias);

    boolean checkIsInterface(ITSPHPAst typeAst, ITypeSymbol symbol);

    boolean checkIsClass(ITSPHPAst typeAst, ITypeSymbol symbol);

    boolean checkVariableIsOkToUse(ITSPHPAst variableId);

    boolean checkIsNotForwardReference(ITSPHPAst ast);

    boolean checkIsNotOutOfConditionalScope(ITSPHPAst ast);

    boolean checkVariableIsInitialised(ITSPHPAst variableId);

    void sendUpInitialisedSymbols(ITSPHPAst blockConditional);

    void sendUpInitialisedSymbolsAfterIf(ITSPHPAst ifBlock, ITSPHPAst elseBlock);

    void sendUpInitialisedSymbolsAfterSwitch(List<ITSPHPAst> conditionalBlocks, boolean hasDefaultLabel);

    void sendUpInitialisedSymbolsAfterTryCatch(List<ITSPHPAst> conditionalBlocks);

    void checkReturnsFromFunction(boolean isReturning, boolean hasAtLeastOneReturnOrThrow, ITSPHPAst identifier);

    void checkReturnsFromMethod(boolean isReturning, boolean hasAtLeastOneReturnOrThrow, ITSPHPAst identifier);

    void checkBreakContinueLevel(ITSPHPAst root, ITSPHPAst level);

    IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPErrorAst typeAst);

    IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPAst typeAst, RecognitionException ex);
}
