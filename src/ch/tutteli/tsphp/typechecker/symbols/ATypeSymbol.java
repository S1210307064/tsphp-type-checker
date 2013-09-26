package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import java.util.HashSet;
import java.util.Set;

public abstract class ATypeSymbol extends ASymbol implements ITypeSymbol
{

    private Set<ITypeSymbol> parentTypeSymbols;

    public ATypeSymbol(final ITSPHPAst theDefinitionAst, final String theName, final ITypeSymbol theParentTypeSymbol) {
        super(theDefinitionAst, theName);
        parentTypeSymbols = new HashSet<>(1);
        parentTypeSymbols.add(theParentTypeSymbol);

    }

    public ATypeSymbol(ITSPHPAst theDefinitionAst, String theName, Set<ITypeSymbol> theParentTypeSymbols) {
        super(theDefinitionAst, theName);
        parentTypeSymbols = theParentTypeSymbols;
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        return parentTypeSymbols;
    }
}
