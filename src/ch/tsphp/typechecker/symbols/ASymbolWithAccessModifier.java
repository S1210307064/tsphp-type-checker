/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

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
