package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.scopes.IScopeHelper;

import java.util.Set;

public class InterfaceTypeSymbol extends APolymorphicTypeSymbol implements IInterfaceTypeSymbol
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
