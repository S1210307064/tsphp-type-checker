package ch.tsphp.typechecker;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.typechecker.symbols.ISymbolWithAccessModifier;
import ch.tsphp.typechecker.symbols.IVariableSymbol;

public interface IAccessResolver
{
    IVariableSymbol resolveClassConstantAccess(ITSPHPAst accessor, ITSPHPAst id);

    IVariableSymbol resolveStaticMemberAccess(ITSPHPAst accessor, ITSPHPAst id);

    IVariableSymbol resolveClassMemberAccess(ITSPHPAst expression, ITSPHPAst identifier);

    void checkVisibility(ISymbolWithAccessModifier methodSymbol, IPolymorphicTypeSymbol polymorphicTypeSymbol,
            IViolationCaller visibilityViolationCaller, ITSPHPAst calleeOrAccessor, ITSPHPAst identifier);

    /**
     * A "delegate" which represents a call of a visibility violation method (usually on an ITypeCheckErrorReporter).
     */
    public interface IViolationCaller
    {
        void callAppropriateMethod(ITSPHPAst identifier, ISymbolWithAccessModifier symbol, int accessFrom);
    }
}
