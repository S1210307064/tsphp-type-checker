package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.ISymbol;

public interface ICreateSymbolListener
{
    void setNewlyCreatedSymbol(ISymbol symbol);
}
