/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;

import java.util.HashSet;

public class VoidTypeSymbol extends ATypeSymbol implements IVoidTypeSymbol
{

    public VoidTypeSymbol() {
        super(null, "void", new HashSet<ITypeSymbol>());
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException("Void has no default value and should not be used as type"
                + " other than return type of a function/method.");
    }
}
