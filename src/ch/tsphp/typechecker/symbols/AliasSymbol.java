/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ASymbol;
import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;

public class AliasSymbol extends ASymbol implements IAliasSymbol
{

    private ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes;

    public AliasSymbol(ITSPHPAst theDefinitionAst, String aliasName) {
        super(theDefinitionAst, aliasName);
    }

    @Override
    public ILowerCaseStringMap<IGlobalNamespaceScope> getGlobalNamespaceScopes() {
        return globalNamespaceScopes;
    }

    @Override
    public void setGlobalNamespaceScopes(ILowerCaseStringMap<IGlobalNamespaceScope> theGlobalNamespaceScopes) {
        globalNamespaceScopes = theGlobalNamespaceScopes;
    }
}
