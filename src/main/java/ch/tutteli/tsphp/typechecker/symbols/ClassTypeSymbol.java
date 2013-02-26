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
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 *
 */
public class ClassTypeSymbol extends AScopedTypeSymbol implements IClassTypeSymbol
{

    private IMethodSymbol construct;
    private IVariableSymbol $this;
    private Set<IInterfaceTypeSymbol> interfaceTypeSymbols = new HashSet<>();

    public ClassTypeSymbol(ITSPHPAst definitionAst, Set<Integer> modifiers, String name, IScope enclosingScope,
            ITypeSymbol parentTypeSymbol) {
        super(definitionAst, modifiers, name, enclosingScope, parentTypeSymbol);
    }

    @Override
    public void setConstruct(IMethodSymbol newConstruct) {
        construct = newConstruct;
    }

    @Override
    public IMethodSymbol getConstruct() {
        return construct;
    }

    @Override
    public IVariableSymbol getThis() {
        return $this;
    }

    @Override
    public void setThis(IVariableSymbol theThis) {
        $this = theThis;
    }

    @Override
    public void addInterface(IInterfaceTypeSymbol interfaceTypeSymbol) {
        interfaceTypeSymbols.add(interfaceTypeSymbol);
    }

    @Override
    public Set<IInterfaceTypeSymbol> getInterfaces() {
        return interfaceTypeSymbols;
    }
}