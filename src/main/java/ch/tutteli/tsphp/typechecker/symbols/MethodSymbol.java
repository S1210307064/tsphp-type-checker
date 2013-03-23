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
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class MethodSymbol extends AScopedSymbol implements IMethodSymbol
{

    private List<IVariableSymbol> parameters = new ArrayList<>();
    private Set<Integer> returnTypeModifier;

    public MethodSymbol(ITSPHPAst definitionAst, Set<Integer> methodModifiers, Set<Integer> theReturnTypeModifier,
            String name, IScope enclosingScope) {
        super(definitionAst, methodModifiers, name, enclosingScope);
        returnTypeModifier = theReturnTypeModifier;
    }

    @Override
    public void addParameter(IVariableSymbol typeSymbol) {
        parameters.add(typeSymbol);
    }

    @Override
    public List<IVariableSymbol> getParameters() {
        return parameters;
    }

    @Override
    public boolean isStatic() {
        return modifiers.contains(TSPHPDefinitionWalker.Static);
    }

    @Override
    public boolean isFinal() {
        return modifiers.contains(TSPHPDefinitionWalker.Final);
    }

    @Override
    public boolean isAbstract() {
        return modifiers.contains(TSPHPDefinitionWalker.Abstract);
    }

    @Override
    public boolean isAlwaysCasting() {
        return returnTypeModifier.contains(TSPHPDefinitionWalker.Cast);
    }

    @Override
    public boolean isPublic() {
        return modifiers.contains(TSPHPDefinitionWalker.Public);
    }

    @Override
    public boolean isProtected() {
        return modifiers.contains(TSPHPDefinitionWalker.Protected);
    }

    @Override
    public boolean isPrivate() {
        return modifiers.contains(TSPHPDefinitionWalker.Private);
    }

    @Override
    public boolean canBeAccessedFrom(int type) {
        return ModifierHelper.canBeAccessedFrom(modifiers, type);
    }

    @Override
    public String toString() {
        return super.toString() + ModifierHelper.getModifiers(new TreeSet(returnTypeModifier));
    }
}
