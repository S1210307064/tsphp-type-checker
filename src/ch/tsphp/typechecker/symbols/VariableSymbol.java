package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

import java.util.Set;

public class VariableSymbol extends ASymbolWithAccessModifier implements IVariableSymbol
{

    public VariableSymbol(ITSPHPAst definitionAst, Set<Integer> modifiers, String name) {
        super(definitionAst, modifiers, name);
    }

    @Override
    public boolean isStatic() {
        return modifiers.contains(TSPHPDefinitionWalker.Static);
    }

    @Override
    public boolean isAlwaysCasting() {
        return modifiers.contains(TSPHPDefinitionWalker.Cast);
    }
}
