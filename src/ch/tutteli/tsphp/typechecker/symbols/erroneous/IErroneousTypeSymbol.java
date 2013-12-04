package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;

public interface IErroneousTypeSymbol extends IErroneousSymbol, IClassTypeSymbol, IInterfaceTypeSymbol
{
}
