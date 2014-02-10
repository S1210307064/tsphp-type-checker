package ch.tsphp.typechecker.symbols.erroneous;

import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IInterfaceTypeSymbol;

public interface IErroneousTypeSymbol extends IErroneousSymbol, IClassTypeSymbol, IInterfaceTypeSymbol
{
}
