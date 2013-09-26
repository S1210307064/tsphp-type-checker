package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceAstTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import java.util.Arrays;
import java.util.Collection;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ResolveInstanceofTest extends AReferenceAstTest
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
                        "class a{} a $a; $a instanceof a;",
                        new ScopeTestStruct[]{
                            instanceOf("a", "\\.\\.", 1, 2, 0, 1)
                        }
                    },
                    {
                        "namespace b{class a{} a $a; $a instanceof a;}",
                        new ScopeTestStruct[]{
                            instanceOf("a", "\\b\\.\\b\\.", 1, 2, 0, 1)
                        }
                    },
                    {
                        "class a{} use a as b; b $a; $a instanceof b;",
                        new ScopeTestStruct[]{
                            instanceOf("b", "\\.\\.", 1, 3, 0, 1)
                        }
                    },
                    {
                        "namespace b{class a{}} namespace x{ use b\\a as b; b $a; $a instanceof b;}",
                        new ScopeTestStruct[]{
                            instanceOf("b", "\\b\\.\\b\\.", 1, 1, 2, 0, 1)
                        }
                    },
                    //variable - see TSPHP-458
                      {
                        "class a{} a $b; a $a; $a instanceof $b;",
                        new ScopeTestStruct[]{
                            instanceOf("$b", "\\.\\.", 1, 3, 0, 1)
                        }
                    },
                    {
                        "namespace b{class a{} a $b; a $a; $a instanceof $b;}",
                        new ScopeTestStruct[]{
                            instanceOf("$b", "\\b\\.\\b\\.", 1, 3, 0, 1)
                        }
                    },
                    {
                        "class a{} use a as b; b $b; b $a; $a instanceof $b;",
                        new ScopeTestStruct[]{
                            instanceOf("$b", "\\.\\.", 1, 4, 0, 1)
                        }
                    },
                    {
                        "namespace b{class a{}} namespace x{ use b\\a as b; b $b; b $a; $a instanceof $b;}",
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
