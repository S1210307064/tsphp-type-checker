/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.utils;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;

public final class ModifierHelper
{
    private ModifierHelper() {
    }

    public static String getModifiersAsString(SortedSet modifiers) {
        String typeModifiers;
        if (modifiers == null || modifiers.size() == 0) {
            typeModifiers = "";
        } else {
            typeModifiers = Arrays.toString(modifiers.toArray());
            typeModifiers = "|" + typeModifiers.substring(1, typeModifiers.length() - 1);
        }
        return typeModifiers;
    }

    public static boolean canBeAccessedFrom(Set<Integer> modifiers, int type) {
        boolean canBeAccessed;
        switch (type) {
            case TSPHPDefinitionWalker.Public:
                canBeAccessed = modifiers.contains(TSPHPDefinitionWalker.Public);
                break;
            case TSPHPDefinitionWalker.Protected:
                canBeAccessed = modifiers.contains(TSPHPDefinitionWalker.Public)
                        || modifiers.contains(TSPHPDefinitionWalker.Protected);
                break;
            case TSPHPDefinitionWalker.Private:
                canBeAccessed = modifiers.contains(TSPHPDefinitionWalker.Public)
                        || modifiers.contains(TSPHPDefinitionWalker.Protected)
                        || modifiers.contains(TSPHPDefinitionWalker.Private);
                break;
            default:
                throw new RuntimeException("Wrong type passed: " + type + " should correspond to "
                        + "TSPHPDefinitionWalker.Public, TSPHPDefinitionWalker.Protected or "
                        + "TSPHPDefinitionWalker.Private");
        }
        return canBeAccessed;
    }
}
