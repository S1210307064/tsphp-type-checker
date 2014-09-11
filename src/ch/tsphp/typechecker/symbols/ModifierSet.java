/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;


import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

import java.util.HashSet;

public class ModifierSet extends HashSet<Integer> implements IModifierSet
{
    @Override
    public boolean isAbstract() {
        return contains(TSPHPDefinitionWalker.Abstract);
    }

    @Override
    public boolean isFinal() {
        return contains(TSPHPDefinitionWalker.Final);
    }

    public boolean isStatic() {
        return contains(TSPHPDefinitionWalker.Static);
    }

    @Override
    public boolean isPublic() {
        return contains(TSPHPDefinitionWalker.Public);
    }

    @Override
    public boolean isProtected() {
        return contains(TSPHPDefinitionWalker.Protected);
    }

    @Override
    public boolean isPrivate() {
        return contains(TSPHPDefinitionWalker.Private);
    }

    public boolean isAlwaysCasting() {
        return contains(TSPHPDefinitionWalker.Cast);
    }

    public boolean isFalseable() {
        return contains(TSPHPDefinitionWalker.LogicNot);
    }

    public boolean isNullable() {
        return contains(TSPHPDefinitionWalker.QuestionMark);
    }
}
