/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.scopes.IScopeHelper;

import java.util.Set;

public class InterfaceTypeSymbol extends APolymorphicTypeSymbol implements IInterfaceTypeSymbol
{

    @SuppressWarnings("checkstyle:parameternumber")
    public InterfaceTypeSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            Set<Integer> modifiers,
            String name,
            IScope theEnclosingScope,
            ITypeSymbol parentTypeSymbol) {
        super(scopeHelper, definitionAst, modifiers, name, theEnclosingScope, parentTypeSymbol);
    }
}
