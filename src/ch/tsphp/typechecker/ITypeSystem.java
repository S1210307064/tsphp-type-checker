package ch.tsphp.typechecker;

import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.INullTypeSymbol;
import ch.tsphp.typechecker.symbols.IPseudoTypeSymbol;
import ch.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tsphp.typechecker.symbols.IVoidTypeSymbol;

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