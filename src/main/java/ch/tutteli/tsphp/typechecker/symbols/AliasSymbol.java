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

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class AliasSymbol extends ASymbol implements IAliasSymbol
{

    private Map<String, IScope> globalNamespaceScopes;

    public AliasSymbol(ITSPHPAst theDefinitionAst, String aliasName) {
        super(theDefinitionAst, aliasName);
    }

    @Override
    public Map<String, IScope> getGlobalNamespaceScopes() {
        return globalNamespaceScopes;
    }

    @Override
    public void setGlobalNamespaceScopes(Map<String, IScope> theGlobalNamespaceScopes) {
        globalNamespaceScopes = theGlobalNamespaceScopes;
    }
}
