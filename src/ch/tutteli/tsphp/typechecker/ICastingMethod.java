package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;

public interface ICastingMethod
{

    ITSPHPAst createCastAst(ITSPHPAst ast);

    ITypeSymbol getType();

    /**
     * Return the parent type which provided the casting method or null if the casting method is defined on the type to
     * be cast.
     *
     * For instance, if B $b; A $a = (A) $b; and not B itself contains the cast to A but C (parent type of B) then this
     * method will return the ITypeSymbol which represents the type C. However, if B contains the casting then null will
     * be returned.
     *
     * @return
     */
    ITypeSymbol getParentTypeWhichProvidesCast();

    void setParentTypeWhichProvidesCast(ITypeSymbol parentTypeSymbol);
}
