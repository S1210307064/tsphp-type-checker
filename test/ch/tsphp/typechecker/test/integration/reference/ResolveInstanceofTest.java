/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceScopeTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ResolveInstanceofTest extends AReferenceScopeTest
{

    public ResolveInstanceofTest(String testString, ScopeTestStruct[] testStructs) {
        super(testString, testStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {
                        "class a{} a $a=null; $a instanceof a;",
                        new ScopeTestStruct[]{instanceOf("a", "\\.\\.", 1, 2, 0, 1)}
                },
                {
                        "namespace b{class a{} a $a=null; $a instanceof a;}",
                        new ScopeTestStruct[]{instanceOf("a", "\\b\\.\\b\\.", 1, 2, 0, 1)}
                },
                {
                        "class a{} use a as b; b $a=null; $a instanceof b;",
                        new ScopeTestStruct[]{
                                instanceOf("b", "\\.\\.", 1, 3, 0, 1)
                        }
                },
                {
                        "namespace b{class a{}} namespace x{ use b\\a as b; b $a=null; $a instanceof b;}",
                        new ScopeTestStruct[]{
                                instanceOf("b", "\\b\\.\\b\\.", 1, 1, 2, 0, 1)
                        }
                },
                //variable - see TSPHP-458
                {
                        "class a{} a $b=null; a $a=null; $a instanceof $b;",
                        new ScopeTestStruct[]{
                                instanceOf("$b", "\\.\\.", 1, 3, 0, 1)
                        }
                },
                {
                        "namespace b{class a{} a $b=null; a $a=null; $a instanceof $b;}",
                        new ScopeTestStruct[]{
                                instanceOf("$b", "\\b\\.\\b\\.", 1, 3, 0, 1)
                        }
                },
                {
                        "class a{} use a as b; b $b=null; b $a=null; $a instanceof $b;",
                        new ScopeTestStruct[]{
                                instanceOf("$b", "\\.\\.", 1, 4, 0, 1)
                        }
                },
                {
                        "namespace b{class a{}} namespace x{ use b\\a as b; b $b=null; b $a=null; $a instanceof $b;}",
                        new ScopeTestStruct[]{
                                instanceOf("$b", "\\x\\.\\x\\.", 1, 1, 3, 0, 1)
                        }
                }
        });
    }

    public static ScopeTestStruct instanceOf(String callee, String scope, Integer... accessToScope) {
        return new ScopeTestStruct(callee, scope, Arrays.asList(accessToScope));
    }
}
