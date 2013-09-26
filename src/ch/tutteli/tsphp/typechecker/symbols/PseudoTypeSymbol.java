package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITypeSymbol;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr
 */
public class PseudoTypeSymbol extends ANullableTypeSymbol implements IPseudoTypeSymbol
{

    public PseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(null, name, parentTypeSymbol);
    }
}