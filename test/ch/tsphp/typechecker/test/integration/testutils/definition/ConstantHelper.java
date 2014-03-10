/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.definition;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.symbols.ModifierHelper;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ConstantHelper
{

    public static Collection<Object[]> testStrings(String prefix, String appendix, String prefixExpected,
            final String scopeName, boolean isDefinitionPhase) {

        List<Object[]> collection = new ArrayList<>();
        String[] types = TypeHelper.getScalarTypes();
        SortedSet<Integer> modifiers = new TreeSet<>(Arrays.asList(new Integer[]{
                TSPHPDefinitionWalker.Public,
                TSPHPDefinitionWalker.Static,
                TSPHPDefinitionWalker.Final
        }));
        String mod = ModifierHelper.getModifiers(modifiers);

        for (String type : types) {
            String typeExpected = isDefinitionPhase ? "" : type;
            collection.add(new Object[]{
                    prefix + "const " + type + " a=true;" + appendix,
                    prefixExpected + scopeName + type + " " + scopeName + "a#" + typeExpected + mod
            });
            collection.add(new Object[]{
                    prefix + "const " + type + " a=true, b=false;" + appendix,
                    prefixExpected + scopeName + type + " " + scopeName + "a#" + typeExpected + mod + " "
                            + scopeName + type + " " + scopeName + "b#" + typeExpected + mod
            });
            collection.add(new Object[]{
                    prefix + "const " + type + " a=1,b=2;" + appendix,
                    prefixExpected + scopeName + type + " " + scopeName + "a#" + typeExpected + mod + " "
                            + scopeName + type + " " + scopeName + "b#" + typeExpected + mod
            });
            collection.add(new Object[]{
                    prefix + "const " + type + " a=1.0,b=2.0,c=null;" + appendix,
                    prefixExpected + scopeName + type + " " + scopeName + "a#" + typeExpected + mod + " "
                            + scopeName + type + " " + scopeName + "b#" + typeExpected + mod + " "
                            + scopeName + type + " " + scopeName + "c#" + typeExpected + mod
            });
            collection.add(new Object[]{
                    prefix + "const " + type + " a=1,b=\"2\",c=null,d='2';" + appendix,
                    prefixExpected + scopeName + type + " " + scopeName + "a#" + typeExpected + mod + " "
                            + scopeName + type + " " + scopeName + "b#" + typeExpected + mod + " "
                            + scopeName + type + " " + scopeName + "c#" + typeExpected + mod + " "
                            + scopeName + type + " " + scopeName + "d#" + typeExpected + mod
            });
        }
        return collection;
    }
}
