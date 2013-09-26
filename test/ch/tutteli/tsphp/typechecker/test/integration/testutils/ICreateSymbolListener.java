package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.common.ISymbol;

public interface ICreateSymbolListener
{
    void setNewlyCreatedSymbol(ISymbol symbol);
}
