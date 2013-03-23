/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public final class ModifierHelper
{

    private ModifierHelper() {
    }

    public static String getModifiers(SortedSet modifiers) {
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
