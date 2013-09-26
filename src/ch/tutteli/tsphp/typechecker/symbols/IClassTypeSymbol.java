package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.typechecker.scopes.ICaseInsensitiveScope;

public interface IClassTypeSymbol extends IPolymorphicTypeSymbol, ICaseInsensitiveScope, ICanBeFinal, ICanBeAbstract
{

    IMethodSymbol getConstruct();

    void setConstruct(IMethodSymbol construct);

    IVariableSymbol getThis();

    void setThis(IVariableSymbol $this);

    IClassTypeSymbol getParent();

    void setParent(IClassTypeSymbol theParent);
}
