package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITypeSymbol;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public class PseudoTypeSymbol extends ANullableTypeSymbol implements IPseudoTypeSymbol
{

    public PseudoTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(name, parentTypeSymbol);
    }
}