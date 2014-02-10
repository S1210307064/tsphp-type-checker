package ch.tsphp.typechecker.symbols;

public interface IClassTypeSymbol extends IPolymorphicTypeSymbol, ICanBeFinal
{

    IMethodSymbol getConstruct();

    void setConstruct(IMethodSymbol construct);

    IVariableSymbol getThis();

    void setThis(IVariableSymbol $this);

    IClassTypeSymbol getParent();

    void setParent(IClassTypeSymbol theParent);
}
