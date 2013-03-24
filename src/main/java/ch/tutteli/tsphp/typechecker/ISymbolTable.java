/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
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

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface ISymbolTable
{

    void initTypeSystem();

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
