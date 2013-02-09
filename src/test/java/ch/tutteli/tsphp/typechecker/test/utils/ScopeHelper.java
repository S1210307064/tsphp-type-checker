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

import ch.tutteli.tsphp.common.IScope;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ScopeHelper
{

    private ScopeHelper() {
    }

    public static String getEnclosingScopeNames(IScope scope) {
        StringBuilder stringBuilder = new StringBuilder();
        while (scope != null) {
            if (isNotDefaultNamespace(scope)) {
                stringBuilder.insert(0, ".");
                stringBuilder.insert(0, scope.getScopeName());
            }
            scope = scope.getEnclosingScope();
        }
        return stringBuilder.toString();
    }

    static boolean isNotDefaultNamespace(IScope scope) {
        return !scope.getScopeName().equals(IScope.DEFAULT_NAMESPACE);
    }

    public static Collection<Object[]> testStringsDefaultNamespace() {
        return testStrings("", "", "global", new Integer[]{1});
    }

    public static Collection<Object[]> testStrings(String prefix, String appendix,
            String fullScopeName, Integer[] accessToScope) {

        fullScopeName += ".";

        return Arrays.asList(new Object[][]{ // nBody.vars.$a.$b
                    {prefix + "int $a = $b;" + appendix, new ScopeTestStruct[]{
                            new ScopeTestStruct("$b", fullScopeName, getAstAccessOrder(accessToScope, 0, 1, 0))
                        }
                    },
                    {prefix + "int $a = $b + $c;" + appendix, new ScopeTestStruct[]{
                            //vars $a + $b
                            new ScopeTestStruct("$b", fullScopeName, getAstAccessOrder(accessToScope, 0, 1, 0, 0)),
                            //vars $a + $c
                            new ScopeTestStruct("$c", fullScopeName, getAstAccessOrder(accessToScope, 0, 1, 0, 1))
                        }
                    },
                    {prefix + "int $a = 1 + $b + $c;" + appendix, new ScopeTestStruct[]{
                            //vars $a + + $b
                            new ScopeTestStruct("$b", fullScopeName, getAstAccessOrder(accessToScope, 0, 1, 0, 0, 1)),
                            //vars $a + $c
                            new ScopeTestStruct("$c", fullScopeName, getAstAccessOrder(accessToScope, 0, 1, 0, 1))
                        }
                    },
                    //
                    {prefix + "$a = $b;" + appendix, new ScopeTestStruct[]{
                            //= $a
                            new ScopeTestStruct("$a", fullScopeName, getAstAccessOrder(accessToScope, 0, 0)),
                            //= $b
                            new ScopeTestStruct("$b", fullScopeName, getAstAccessOrder(accessToScope, 0, 1))
                        }
                    },
                    //there are no nested local scopes
                    {prefix + " { $a = $b; } " + appendix, new ScopeTestStruct[]{
                            //= $a
                            new ScopeTestStruct("$a", fullScopeName, getAstAccessOrder(accessToScope, 0, 0)),
                            //= $b
                            new ScopeTestStruct("$b", fullScopeName, getAstAccessOrder(accessToScope, 0, 1))
                        }
                    },
                    //there are no nested local scopes, does not matter how many {} we declare
                    {prefix + " { { $a = $b;} int $a = $c; } " + appendix, new ScopeTestStruct[]{
                            //= $a
                            new ScopeTestStruct("$a", fullScopeName, getAstAccessOrder(accessToScope, 0, 0)),
                            //= $b
                            new ScopeTestStruct("$b", fullScopeName, getAstAccessOrder(accessToScope, 0, 1)),
                            //vars $a $b
                            new ScopeTestStruct("$c", fullScopeName, getAstAccessOrder(accessToScope, 1, 1, 0))
                        }
                    }
                });
    }

    private static List<Integer> getAstAccessOrder(Integer[] accessToScope, Integer... accessToTestCandidate) {
        List<Integer> acessOrder = new ArrayList<>();
        acessOrder.addAll(Arrays.asList(accessToScope));
        acessOrder.addAll(Arrays.asList(accessToTestCandidate));
        return acessOrder;
    }
}
