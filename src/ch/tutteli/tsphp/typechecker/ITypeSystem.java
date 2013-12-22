package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.INullTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPseudoTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVoidTypeSymbol;

import java.util.List;
import java.util.Map;

public interface ITypeSystem
{

    Map<Integer, List<IMethodSymbol>> getUnaryOperators();

    Map<Integer, List<IMethodSymbol>> getBinaryOperators();

    Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> getExplicitCastings();

    IVoidTypeSymbol getVoidTypeSymbol();

    INullTypeSymbol getNullTypeSymbol();

    IScalarTypeSymbol getBoolTypeSymbol();

    IScalarTypeSymbol getBoolNullableTypeSymbol();

    IScalarTypeSymbol getIntTypeSymbol();

    IScalarTypeSymbol getIntNullableTypeSymbol();

    IScalarTypeSymbol getFloatTypeSymbol();

    IScalarTypeSymbol getFloatNullableTypeSymbol();

    IScalarTypeSymbol getStringTypeSymbol();

    IScalarTypeSymbol getStringNullableTypeSymbol();

    IArrayTypeSymbol getArrayTypeSymbol();

    IPseudoTypeSymbol getResourceTypeSymbol();

    IPseudoTypeSymbol getObjectTypeSymbol();

    IClassTypeSymbol getExceptionTypeSymbol();

    ICastingMethod getStandardCastingMethod(ITypeSymbol formalParameter);
}
