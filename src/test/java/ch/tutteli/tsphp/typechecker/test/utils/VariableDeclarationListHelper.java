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

    public static Collection<Object[]> testStringsDefinitionPhase(String prefix, String appendix, String scope) {
        return testStrings(prefix, appendix, scope, true);
    }

    private static Collection<Object[]> testStrings(String prefix, String appendix, String scope, boolean isDefinitionPhase) {

        scope = !scope.isEmpty() ? scope + "." : scope;

        List<Object[]> collection = TypeHelper.getAllTypesInclModifier(
                prefix, "$a" + appendix, scope + "$a", "", isDefinitionPhase);

        collection.addAll(TypeHelper.getAllTypesInclModifier(
                prefix, "$a=1" + appendix, scope + "$a", "", isDefinitionPhase));

        String typeExpected = isDefinitionPhase ? "" : "int";
        collection.addAll(getVariations(prefix + "int", "=", appendix, scope, typeExpected));
        typeExpected = isDefinitionPhase ? "" : "object";
        collection.addAll(getVariations(prefix + "object", "=", appendix, scope, typeExpected));
        typeExpected = isDefinitionPhase ? "" : "float";
        collection.addAll(getVariations(prefix + "float", "=()", appendix, scope, typeExpected));

        typeExpected = isDefinitionPhase ? "" : "int";
        collection.addAll(Arrays.asList(new Object[][]{
                    {prefix + "int $a                    " + appendix, scope + "$a" + typeExpected},
                    {prefix + "int $a,     $b            " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected},
                    {prefix + "int $a,     $b,     $c    " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b=1,   $c    " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b,     $c=1  " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b=1,   $c=1  " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b=()1, $c    " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b=()1, $c=1  " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b,     $c=()1" + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b=1,   $c=()1" + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=()1, $b=()1, $c=()1" + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a,     $b=()1, $c    " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a,     $b=()1, $c=1  " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a,     $b=()1, $c=()1" + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a,     $b=1,   $c=()1" + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=1,   $b=()1, $c    " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=1,   $b=()1, $c=1  " + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=1,   $b=()1, $c=()1" + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected},
                    {prefix + "int $a=1,   $b=1,   $c=()1" + appendix, scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected}
                }));
        return collection;
    }

    private static Collection<Object[]> getVariations(String prefix, String operator, String appendix,
            String scope, String typeExpected) {
        return Arrays.asList(new Object[][]{
                    {
                        prefix + " $a, $b, $c" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b, $c" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b" + operator + "1, $c" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b, $c" + operator + "1" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    },
                    {
                        prefix + " $a" + operator + "1, $b" + operator + "1, $c" + operator + "1" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b" + operator + "1, $c" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b" + operator + "1, $c" + operator + "1" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    },
                    {
                        prefix + " $a, $b, $c" + operator + "1" + appendix,
                        scope + "$a" + typeExpected + " " + scope + "$b" + typeExpected + " " + scope + "$c" + typeExpected
                    }
                });
    }
}
