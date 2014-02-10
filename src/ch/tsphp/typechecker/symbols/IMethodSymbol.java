package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;

import java.util.List;

public interface IMethodSymbol extends ISymbolWithAccessModifier, IScope,
        ICanBeStatic, ICanBeFinal, ICanBeAbstract, ICanAlwaysCast
{

    void addParameter(IVariableSymbol variableSymbol);

    List<IVariableSymbol> getParameters();
}
