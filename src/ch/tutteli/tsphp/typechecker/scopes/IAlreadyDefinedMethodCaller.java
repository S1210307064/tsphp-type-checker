package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ISymbol;

public interface IAlreadyDefinedMethodCaller
{
    void callAccordingAlreadyDefinedMethod(ISymbol firstDefinition, ISymbol symbolToCheck);
}
