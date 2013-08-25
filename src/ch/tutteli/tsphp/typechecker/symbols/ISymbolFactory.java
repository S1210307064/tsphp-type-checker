/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousAccessSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public interface ISymbolFactory
{

    void setObjectTypeSymbol(ITypeSymbol typeSymbol);

    INullTypeSymbol createNullTypeSymbol();

    IVoidTypeSymbol createVoidTypeSymbol();

    IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenType, ITypeSymbol parentTypeSymbol,
            boolean isNullable, int defaultValueTokenType, String defaultValue);

    IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenType, Set<ITypeSymbol> parentTypeSymbol,
            boolean isNullable, int defaultValueTokenType, String defaultValue);

    IArrayTypeSymbol createArrayTypeSymbol(String name, int tokenType, ITypeSymbol keyValue, ITypeSymbol valueType);

    IPseudoTypeSymbol createPseudoTypeSymbol(String name);

    IAliasSymbol createAliasSymbol(ITSPHPAst useDefinition, String alias);

    IAliasTypeSymbol createAliasTypeSymbol(ITSPHPAst definitionAst, String name);

    IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier, IScope currentScope);

    IClassTypeSymbol createClassTypeSymbol(ITSPHPAst classModifier, ITSPHPAst identifier, IScope currentScope);

    IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier, ITSPHPAst identifier,
            IScope currentScope);

    IVariableSymbol createThisSymbol(ITSPHPAst variableId, IPolymorphicTypeSymbol polymorphicTypeSymbol);

    IVariableSymbol createVariableSymbol(ITSPHPAst typeModifier, ITSPHPAst variableId);

    IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPAst ast, TypeCheckerException exception);

    IErroneousAccessSymbol createErroneousAccessSymbol(ITSPHPAst ast, TypeCheckerException exception);

    IErroneousClassTypeSymbol createErroneousClassTypeSymbol(ITSPHPAst ast, TypeCheckerException ex);

    IErroneousMethodSymbol createErroneousMethodSymbol(ITSPHPAst ast, TypeCheckerException ex);

    IVariableSymbol createErroneousVariableSymbol(ITSPHPAst ast, TypeCheckerException exception);
}
