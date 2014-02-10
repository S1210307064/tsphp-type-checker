package ch.tsphp.typechecker.symbols.erroneous;

import ch.tsphp.common.ISymbol;
import ch.tsphp.common.exceptions.TSPHPException;

/**
 * Marker interface for erroneous symbols.
 */
public interface IErroneousSymbol extends ISymbol
{
    TSPHPException getException();
}
