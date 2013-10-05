package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;

import java.util.Set;

public class InterfaceTypeSymbol extends AScopedTypeSymbol implements IInterfaceTypeSymbol
{

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
