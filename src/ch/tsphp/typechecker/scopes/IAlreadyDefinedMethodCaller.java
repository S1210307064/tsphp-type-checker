package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.ISymbol;

public interface IAlreadyDefinedMethodCaller
{
    void callAccordingAlreadyDefinedMethod(ISymbol firstDefinition, ISymbol symbolToCheck);
}
