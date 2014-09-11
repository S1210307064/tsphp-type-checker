/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;

import java.util.HashSet;

public class NullTypeSymbol extends ANullableTypeSymbol implements INullTypeSymbol
{

    public NullTypeSymbol() {
        super("null", new HashSet<ITypeSymbol>());
    }
}
