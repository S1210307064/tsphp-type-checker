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
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class SymbolFactory implements ISymbolFactory
{

    @Override
    public IScalarTypeSymbol createScalarTypeSymbol(String name) {
        return new ScalarTypeSymbol(name);
    }

    @Override
    public IArrayTypeSymbol createArrayTypeSymbol(String name, ITypeSymbol baseType) {
        return new ArrayTypeSymbol(name, baseType);
    }

    @Override
    public IPseudoTypeSymbol createPseudoTypeSymbol(String name) {
        return new PseudoTypeSymbol(name);
    }

    @Override
    public IAliasSymbol createAliasSymbol(TSPHPAst useDefinition, String alias) {
        return new AliasSymbol(useDefinition, alias);
    }

    @Override
    public IClassSymbol createClassSymbol(TSPHPAst classModifierAst, TSPHPAst identifier, IScope currentScope) {
        return new ClassSymbol(identifier, getModifiers(classModifierAst), identifier.getText(), currentScope);
    }

    @Override
    public IMethodSymbol createMethodSymbol(TSPHPAst methodModifier, TSPHPAst returnTypeModifier, TSPHPAst identifier,
            IScope currentScope) {
        return new MethodSymbol(identifier, getModifiers(methodModifier), getModifiers(returnTypeModifier),
                identifier.getText(), currentScope);
    }

    @Override
    public IVariableSymbol createVariableSymbol(TSPHPAst typeModifier, TSPHPAst variableId) {
        return new VariableSymbol(variableId, getModifiers(typeModifier), variableId.getText());
    }

    private Set<Integer> getModifiers(TSPHPAst modifierAst) {
        Set<Integer> modifiers = new HashSet<>();

        List<TSPHPAst> children = (List<TSPHPAst>) modifierAst.getChildren();
        if (children != null && !children.isEmpty()) {
            for (TSPHPAst child : children) {
                modifiers.add(child.getType());
            }
        }
        return modifiers;
    }
}
