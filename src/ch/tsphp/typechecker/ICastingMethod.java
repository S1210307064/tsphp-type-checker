/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;

/**
 * Represents a casting method, e.g. built-in casting method.
 * <p/>
 * To outline the example, a quick code example and the corresponding translation to PHP 5.4
 * <pre>
 *     int $i = (int) 1.2;
 * </pre>
 * translation
 * <pre>
 *     $i = (int) 1.2;
 * </pre>
 * <p/>
 * (int) is the built-in casting method.
 */
public interface ICastingMethod
{

    ITSPHPAst createCastAst(ITSPHPAst ast);

    ITypeSymbol getType();

    /**
     * Return the parent type which provided the casting method or null if the casting method is defined on the type to
     * be cast.
     * <p/>
     * For instance, if B $b; A $a = (A) $b; and not B itself contains the cast to A but C (parent type of B) then this
     * method will return the ITypeSymbol which represents the type C. However, if B contains the casting then null will
     * be returned.
     */
    ITypeSymbol getParentTypeWhichProvidesCast();

    void setParentTypeWhichProvidesCast(ITypeSymbol parentTypeSymbol);
}
