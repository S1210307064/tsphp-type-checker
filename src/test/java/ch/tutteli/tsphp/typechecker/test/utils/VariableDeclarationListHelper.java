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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class VariableDeclarationListHelper
{

    private VariableDeclarationListHelper() {
    }

    public static Collection<Object[]> testStringsDefinitionPhase(String prefix, String appendix, String scopeName) {
        return testStrings(prefix, appendix, scopeName, true);
    }

    private static Collection<Object[]> testStrings(final String prefix, final String appendix,
            String scope, final boolean isDefinitionPhase) {

        final String scopeName = !scope.isEmpty() ? scope + "." : scope;

        final List<Object[]> collection = new ArrayList<>();
        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, String typeModifier) {
                String typeExpected2 = isDefinitionPhase ? "" : typeExpected;

                collection.add(new Object[]{
                            prefix + type + "$a" + appendix,
                            scopeName + typeExpected + " " + scopeName + "$a" + typeExpected2 + typeModifier
                        });
                collection.add(new Object[]{
                            prefix + type + "$a=1" + appendix,
                            scopeName + typeExpected + " " + scopeName + "$a" + typeExpected2 + typeModifier
                        });
            }
        });

        String typeExpected = isDefinitionPhase ? "" : "int";
        collection.addAll(getVariations(prefix + "int", "=", appendix, scopeName,"int", typeExpected));
        typeExpected = isDefinitionPhase ? "" : "object";
        collection.addAll(getVariations(prefix + "object", "=", appendix, scopeName,"object", typeExpected));
        typeExpected = isDefinitionPhase ? "" : "float";
        collection.addAll(getVariations(prefix + "float", "=()", appendix, scopeName,"float", typeExpected));

        typeExpected = isDefinitionPhase ? "" : "int";
        collection.addAll(Arrays.asList(new Object[][]{
                    {
                        prefix + "int $a                    " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected
                    },
                    {
                        prefix + "int $a,     $b            " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected
                    },
                    {
                        prefix + "int $a,     $b,     $c    " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=1,   $c    " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b,     $c=1  " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=1,   $c=1  " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=()1, $c    " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=()1, $c=1  " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b,     $c=()1" + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=1,   $c=()1" + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=()1, $b=()1, $c=()1" + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=()1, $c    " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=()1, $c=1  " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=()1, $c=()1" + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a,     $b=1,   $c=()1" + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=()1, $c    " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=()1, $c=1  " + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=()1, $c=()1" + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected},
                    {
                        prefix + "int $a=1,   $b=1,   $c=()1" + appendix,
                        scopeName + "int " + scopeName + "$a" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$b" + typeExpected + " "
                        + scopeName + "int " + scopeName + "$c" + typeExpected}
                }));
        return collection;
    }

    private static Collection<Object[]> getVariations(String prefix, String operator, String appendix,
            String scopeName, String type, String typeExpected) {
        return Arrays.asList(new Object[][]{
                    {
                        prefix + " $a, $b, $c" + appendix,
                        scopeName + type + " " + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b, $c" + appendix,
                        scopeName + type + " "  + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b" + operator + "1, $c" + appendix,
                        scopeName + type + " "  + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b, $c" + operator + "1" + appendix,
                        scopeName + type + " "  + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b" + operator + "1, $c" + operator + "1" + appendix,
                        scopeName + type + " "  + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b" + operator + "1, $c" + appendix,
                        scopeName + type + " "  + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b" + operator + "1, $c" + operator + "1" + appendix,
                        scopeName + type + " "  + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b, $c" + operator + "1" + appendix,
                        scopeName + type + " "  + scopeName + "$a" + typeExpected + " " + scopeName + type + " "  + scopeName + "$b" + typeExpected + " " + scopeName + type + " "  + scopeName + "$c" + typeExpected
                    }
                });
    }
}
