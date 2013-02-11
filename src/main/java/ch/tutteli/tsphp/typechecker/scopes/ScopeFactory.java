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
package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ScopeFactory implements IScopeFactory
{

    private IScope globalScope = new GlobalScope();
    private Map<String, IScope> globalNamespaces = new HashMap<>();
    
    @Override
    public IScope getGlobalScope() {
        return globalScope;
    }
    
    @Override
    public Map<String, IScope> getGlobalNamespaceScopes(){
        return globalNamespaces;
    }
    

    @Override
    public INamespaceScope createNamespace(String name) {
        return new NamespaceScope(name, getOrCreateGlobalNamespace(name));
    }

    private IScope getOrCreateGlobalNamespace(String name) {
        IScope scope;
        if (globalNamespaces.containsKey(name)) {
            scope = globalNamespaces.get(name);
        } else {
            scope = new GlobalNamespaceScope(name, globalScope);
            globalNamespaces.put(name, scope);
        }
        return scope;
    }

    @Override
    public IConditionalScope createConditionalScope(IScope currentScope) {
        return new ConditionalScope(currentScope);
    }
}
