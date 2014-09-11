/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.symbols.ITypeSymbol;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public class PseudoTypeSymbol extends ANullableTypeSymbol implements IPseudoTypeSymbol
{

    public PseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(name, parentTypeSymbol);
    }
}