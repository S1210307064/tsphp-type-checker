/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.symbols.ITypeSymbol;
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

    Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> getImplicitCasting();

    Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> getExplicitCastings();

    IVoidTypeSymbol getVoidTypeSymbol();

    INullTypeSymbol getNullTypeSymbol();

    IScalarTypeSymbol getBoolTypeSymbol();

    IScalarTypeSymbol getBoolFalseableTypeSymbol();

    IScalarTypeSymbol getBoolNullableTypeSymbol();

    IScalarTypeSymbol getBoolFalseableAndNullableTypeSymbol();

    IScalarTypeSymbol getIntTypeSymbol();

    IScalarTypeSymbol getIntFalseableTypeSymbol();

    IScalarTypeSymbol getIntNullableTypeSymbol();

    IScalarTypeSymbol getIntFalseableAndNullableTypeSymbol();

    IScalarTypeSymbol getFloatTypeSymbol();

    IScalarTypeSymbol getFloatFalseableTypeSymbol();

    IScalarTypeSymbol getFloatNullableTypeSymbol();

    IScalarTypeSymbol getFloatFalseableAndNullableTypeSymbol();

    IScalarTypeSymbol getStringTypeSymbol();

    IScalarTypeSymbol getStringFalseableTypeSymbol();

    IScalarTypeSymbol getStringNullableTypeSymbol();

    IScalarTypeSymbol getStringFalseableAndNullableTypeSymbol();

    IArrayTypeSymbol getArrayTypeSymbol();

    IArrayTypeSymbol getArrayFalseableTypeSymbol();

    IPseudoTypeSymbol getResourceTypeSymbol();

    IPseudoTypeSymbol getResourceFalseableTypeSymbol();

    IPseudoTypeSymbol getMixedTypeSymbol();

    IClassTypeSymbol getExceptionTypeSymbol();

    ICastingMethod getStandardCastingMethod(ITypeSymbol formalParameterType);

    void addExplicitCastFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType);

    void addExplicitCastFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType,
            ICastingMethod castingMethod);

    void addImplicitCastFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType);

    void addImplicitCastFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType,
            ICastingMethod castingMethod);


}
