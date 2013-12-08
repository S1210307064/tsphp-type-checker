package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;

/**
 * Marker interface for erroneous symbols.
 */
public interface IErroneousSymbol extends ISymbol
{
    TSPHPException getException();
}
