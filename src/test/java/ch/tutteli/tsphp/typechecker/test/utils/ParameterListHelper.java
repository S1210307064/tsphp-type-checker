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
package ch.tutteli.tsphp.typechecker.test.utils;

import ch.tutteli.tsphp.typechecker.TSPHPTypeCheckerDefinition;
import ch.tutteli.tsphp.typechecker.symbols.ModifierHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ParameterListHelper
{

    private ParameterListHelper() {
    }

    public static Collection<Object[]> getTestStrings(final String prefix, final String appendix,
            final String prefixExpected, final String scopeName, final boolean isDefinitionPhase) {

        //check all types
        final List<Object[]> collection = new ArrayList<>();
        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet<Integer> modifiers) {
                String typeExpected2 = isDefinitionPhase ? "" : typeExpected;

                String typeModifiers = ModifierHelper.getModifiers(modifiers);
                collection.add(new Object[]{
                            prefix + type + "$a" + appendix,
                            prefixExpected + scopeName + typeExpected + " "
                            + scopeName + "$a" + typeExpected2 + typeModifiers
                        });
                collection.add(new Object[]{
                            prefix + type + "$a=1" + appendix,
                            prefixExpected + scopeName + typeExpected + " "
                            + scopeName + "$a" + typeExpected2 + typeModifiers
                        });
            }
        });

        int qMark = TSPHPTypeCheckerDefinition.QuestionMark;
        int cast = TSPHPTypeCheckerDefinition.Cast;

        //all types can have the ? modifier not only scalars for parameters
        List<String> types = TypeHelper.getAllTypesWithoutScalar();
        for (String type : types) {
            String typeExpected = isDefinitionPhase ? "" : type;
            collection.add(new Object[]{
                        prefix + type + "? $a" + appendix,
                        prefixExpected + scopeName + type + " "
                        + scopeName + "$a" + typeExpected + "|" + qMark
                    });
        }

        String typeExpected = isDefinitionPhase ? "" : "int";

        //normal
        collection.addAll(getVariations(
                prefix, "int $a", appendix,
                prefixExpected, scopeName + "int " + scopeName + "$a" + typeExpected,
                scopeName, isDefinitionPhase));
        //cast 
        collection.addAll(getVariations(
                prefix, "cast int $a", appendix,
                prefixExpected, scopeName + "int " + scopeName + "$a" + typeExpected + "|" + cast,
                scopeName, isDefinitionPhase));
        //?
        collection.addAll(getVariations(
                prefix, "int? $a", appendix,
                prefixExpected, scopeName + "int " + scopeName + "$a" + typeExpected + "|" + qMark,
                scopeName, isDefinitionPhase));
        //cast and ? mixed
        collection.addAll(getVariations(
                prefix, "cast int? $a", appendix,
                prefixExpected, scopeName + "int " + scopeName + "$a" + typeExpected + "|" + cast + ", " + qMark,
                scopeName, isDefinitionPhase));

        collection.addAll(getVariationsForOptional(prefix, appendix, prefixExpected, scopeName, isDefinitionPhase));

        return collection;
    }

    private static Collection<Object[]> getVariations(String prefix, String param, String appendix,
            String prefixExpected, String paramExpected, String scopeName, boolean isDefinitionPhase) {

        String typeExpected = isDefinitionPhase ? "" : "int";

        return Arrays.asList(new Object[][]{
                    {
                        prefix + param + appendix,
                        prefixExpected + paramExpected
                    },
                    {
                        prefix + param + ", " + param + appendix,
                        prefixExpected + paramExpected + " " + paramExpected
                    },
                    {
                        prefix + param + ", int $b, int $c" + appendix,
                        prefixExpected
                        + paramExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + param + ", " + param + ", int $c" + appendix,
                        prefixExpected
                        + paramExpected + " "
                        + paramExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + param + ", int $b," + param + "" + appendix,
                        prefixExpected
                        + paramExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + paramExpected
                    },
                    {
                        prefix + "int $a, " + param + ", int $c" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + paramExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + "int $a, " + param + ", " + param + "" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + paramExpected + " "
                        + paramExpected
                    },
                    {
                        prefix + param + ", " + param + ", " + param + "" + appendix,
                        prefixExpected
                        + paramExpected + " "
                        + paramExpected + " "
                        + paramExpected
                    }
                });
    }

    private static Collection<Object[]> getVariationsForOptional(String prefix, String appendix,
            String prefixExpected, String scopeName, boolean isDefinitionPhase) {

        String typeExpected = isDefinitionPhase ? "" : "int";
        int qMark = TSPHPTypeCheckerDefinition.QuestionMark;
        int cast = TSPHPTypeCheckerDefinition.Cast;

        List<Object[]> collection = new ArrayList<>();
        collection.addAll(Arrays.asList(new Object[][]{
                    //optional parameter
                    {
                        prefix + "int $a, int $b='hallo'" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected
                    },
                    {
                        prefix + "int $a, int? $i, int $b=+1" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$i" + typeExpected + "|" + qMark + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected
                    },
                    {
                        prefix + "int $a,cast int? $i, int $b=-10, int $c=2.0" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$i" + typeExpected + "|" + cast + ", " + qMark + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + "int? $a=null,int $b=true, int $c=E_ALL" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + "|" + qMark + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + "int $a, int $b=false, int $c=null" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + "int $a, int $b, int $c=true" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + "cast int $a=1, int? $b=2, cast int $c=3" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected + "|" + cast + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + "|" + qMark + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected + "|" + cast
                    }
                }));


        String[] types = TypeHelper.getClassInterfaceTypes();

        for (String type : types) {
            collection.add(new Object[]{
                        prefix + "int $a=" + type + "::a" + appendix,
                        prefixExpected
                        + scopeName + "int " + scopeName + "$a" + typeExpected
                    });
        }
        return collection;
    }
}