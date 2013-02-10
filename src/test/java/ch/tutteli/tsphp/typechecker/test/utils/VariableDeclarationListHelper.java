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
public class VariableDeclarationListHelper
{

    private VariableDeclarationListHelper() {
    }

    public static Collection<Object[]> testStringsDefinitionPhase(String prefix, String appendix,
            String prefixExpected, String scopeName, SortedSet<Integer> modifier) {
        return testStrings(prefix, appendix, prefixExpected, scopeName, modifier, true);
    }

    private static Collection<Object[]> testStrings(final String prefix, final String appendix,
            final String prefixExpected, String scope, final SortedSet<Integer> modifier, final boolean isDefinitionPhase) {

        final String scopeName = "global." + (scope.isEmpty() ? "" : scope + ".");

        final List<Object[]> collection = new ArrayList<>();
        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet modifiers) {
                String typeExpected2 = isDefinitionPhase ? "" : typeExpected;
                if (modifier != null) {
                    modifiers.addAll(modifier);
                }
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

        String typeModifiers = ModifierHelper.getModifiers(modifier);

        String typeExpected = (isDefinitionPhase ? "" : "int") + typeModifiers;
        collection.addAll(getVariations(prefix + "int", "=", appendix,
                prefixExpected, scopeName, "int", typeExpected));

        typeExpected = (isDefinitionPhase ? "" : "object") + typeModifiers;
        collection.addAll(getVariations(prefix + "object", "=", appendix,
                prefixExpected, scopeName, "object", typeExpected));

        typeExpected = (isDefinitionPhase ? "" : "float") + typeModifiers;
        collection.addAll(getVariations(prefix + "float", "=()", appendix,
                prefixExpected, scopeName, "float", typeExpected));

        typeExpected = (isDefinitionPhase ? "" : "int") + typeModifiers;
        collection.addAll(Arrays.asList(new Object[][]{
                    {
                        prefix + "int $a                    " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected
                    },
                    {
                        prefix + "int $a,     $b            " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected
                    },
                    {
                        prefix + "int $a,     $b,     $c    " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=1,   $c    " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b,     $c=1  " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=1,   $c=1  " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=()1, $c    " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=()1, $c=1  " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b,     $c=()1" + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=1,   $c=()1" + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=()1, $c=()1" + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=()1, $c    " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=()1, $c=1  " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=()1, $c=()1" + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=1,   $c=()1" + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=()1, $c    " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=()1, $c=1  " + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=()1, $c=()1" + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=1,   $c=()1" + appendix,
                        prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected}
                }));
        return collection;
    }

    private static Collection<Object[]> getVariations(String prefix, String operator, String appendix,
            String prefixExpected, String scopeName, String type, String typeExpected) {
        System.out.println(prefix + " $a" + operator + "1, $b, $c" + appendix);
        return Arrays.asList(new Object[][]{
//                    {
//                        prefix + " $a, $b, $c" + appendix,
//                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
//                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
//                        + scopeName + type + " " + scopeName + "$c" + typeExpected
//                    },
                    {
                        prefix + " $a" + operator + "1, $b, $c" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b" + operator + "1, $c" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b, $c" + operator + "1" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b" + operator + "1, $c" + operator + "1" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b" + operator + "1, $c" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b" + operator + "1, $c" + operator + "1" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b, $c" + operator + "1" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "$a" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$b" + typeExpected + " "
                        + scopeName + type + " " + scopeName + "$c" + typeExpected
                    }
                });
    }
}
