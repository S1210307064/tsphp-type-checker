/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

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

/**
 * Represents the type system of TSPHP.
 * <p/>
 * It provides methods to retrieve built-in type symbols such as bool, int, float etc.
 */
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

    IPseudoTypeSymbol getMixedTypeSymbol();

    IClassTypeSymbol getExceptionTypeSymbol();

    ICastingMethod getStandardCastingMethod(ITypeSymbol formalParameter);
}
