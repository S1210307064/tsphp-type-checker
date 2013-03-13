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
package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.ErroneousAccessSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.ErroneousClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.ErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.ErroneusTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.ErroneusVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousAccessSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class SymbolFactory implements ISymbolFactory
{

    private ITypeSymbol objectTypeSymbol = null;

    @Override
    public void setObjectTypeSymbol(ITypeSymbol typeSymbol) {
        objectTypeSymbol = typeSymbol;
    }

    @Override
    public INullTypeSymbol createNullTypeSymbol() {
        return new NullTypeSymbol();

    }

    @Override
    public IVoidTypeSymbol createVoidTypeSymbol() {
        return new VoidTypeSymbol();

    }

    @Override
    public IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenType, ITypeSymbol parentTypeSymbol,
            boolean isNullable) {
        return new ScalarTypeSymbol(name, parentTypeSymbol, tokenType, isNullable);
    }

    @Override
    public IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenType, Set<ITypeSymbol> parentTypeSymbol,
            boolean isNullable) {
        return new ScalarTypeSymbol(name, parentTypeSymbol, tokenType, isNullable);
    }

    @Override
    public IArrayTypeSymbol createArrayTypeSymbol(String name, int tokenType,
            ITypeSymbol keyValue, ITypeSymbol valueType) {
        return new ArrayTypeSymbol(name, tokenType, keyValue, valueType, objectTypeSymbol);
    }

    @Override
    public IPseudoTypeSymbol createPseudoTypeSymbol(String name) {
        return new PseudoTypeSymbol(name, objectTypeSymbol);
    }

    @Override
    public IAliasSymbol createAliasSymbol(ITSPHPAst useDefinition, String alias) {
        return new AliasSymbol(useDefinition, alias);
    }

    @Override
    public IAliasTypeSymbol createAliasTypeSymbol(ITSPHPAst definitionAst, String name) {
        return new AliasTypeSymbol(definitionAst, name, objectTypeSymbol);
    }

    @Override
    public IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier,
            IScope currentScope) {
        return new InterfaceTypeSymbol(identifier, getModifiers(modifier), identifier.getText(), currentScope,
                objectTypeSymbol);
    }

    @Override
    public IClassTypeSymbol createClassTypeSymbol(ITSPHPAst classModifierAst, ITSPHPAst identifier,
            IScope currentScope) {
        return new ClassTypeSymbol(identifier, getModifiers(classModifierAst), identifier.getText(), currentScope,
                objectTypeSymbol);
    }

    @Override
    public IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier,
            ITSPHPAst identifier, IScope currentScope) {
        return new MethodSymbol(identifier, getModifiers(methodModifier), getModifiers(returnTypeModifier),
                identifier.getText(), currentScope);
    }

    @Override
    public IVariableSymbol createThisSymbol(ITSPHPAst variableId, IPolymorphicTypeSymbol polymorphicTypeSymbol) {
        return new ThisSymbol(variableId, variableId.getText(), polymorphicTypeSymbol);
    }

    @Override
    public IVariableSymbol createVariableSymbol(ITSPHPAst typeModifier, ITSPHPAst variableId) {
        Set<Integer> modifiers = typeModifier != null ? getModifiers(typeModifier) : new HashSet<Integer>();
        return new VariableSymbol(variableId, modifiers, variableId.getText());
    }

    @Override
    public IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPAst ast, TypeCheckerException exception) {
        return new ErroneusTypeSymbol(ast, exception);
    }

    @Override
    public IErroneousClassTypeSymbol createErroneousClassSymbol(ITSPHPAst ast, TypeCheckerException ex) {
        IMethodSymbol methodSymbol = createErroneousMethodSymbol(ast, ex);
        return new ErroneousClassTypeSymbol(ast, ex, methodSymbol);
    }

    @Override
    public IErroneousMethodSymbol createErroneousMethodSymbol(ITSPHPAst ast, TypeCheckerException ex) {
        return new ErroneousMethodSymbol(ast, ex);
    }

    @Override
    public IVariableSymbol createErroneousVariableSymbol(ITSPHPAst ast, TypeCheckerException exception) {
        return new ErroneusVariableSymbol(ast, exception);
    }

    @Override
    public IErroneousAccessSymbol createErroneousAccessSymbol(ITSPHPAst ast, TypeCheckerException exception) {
        return new ErroneousAccessSymbol(ast, exception);
    }

    private Set<Integer> getModifiers(ITSPHPAst modifierAst) {
        Set<Integer> modifiers = new HashSet<>();

        List<ITSPHPAst> children = (List<ITSPHPAst>) modifierAst.getChildren();
        if (children != null && !children.isEmpty()) {
            for (ITSPHPAst child : children) {
                modifiers.add(child.getType());
            }
        }
        return modifiers;
    }
}