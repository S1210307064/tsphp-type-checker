package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolWithAccessModifier;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;

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
