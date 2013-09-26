package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import java.util.List;

public interface IMethodSymbol extends ISymbolWithAccessModifier, IScope,
        ICanBeStatic, ICanBeFinal, ICanBeAbstract, ICanAlwaysCast
{

    void addParameter(IVariableSymbol variableSymbol);

    List<IVariableSymbol> getParameters();
}
