package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.Set;

public abstract class ASymbolWithAccessModifier extends ASymbolWithModifier implements ISymbolWithAccessModifier
{

    public ASymbolWithAccessModifier(ITSPHPAst definitionAst, Set<Integer> modifiers, String name) {
        super(definitionAst, modifiers, name);
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
}
