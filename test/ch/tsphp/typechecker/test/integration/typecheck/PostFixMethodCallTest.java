/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.reference.ReferenceScopeTestStruct;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.AReferenceScopeTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PostFixMethodCallTest extends AReferenceScopeTypeCheckTest
{

    public PostFixMethodCallTest(String testString, ReferenceScopeTestStruct[] scopeTestStructs,
            TypeCheckStruct[] typeCheckStructs) {
        super(testString, scopeTestStructs, typeCheckStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        String dfault = "\\.\\.";
        return Arrays.asList(new Object[][]{
                {"function Exception foo(){return new Exception;}"
                        + "foo()->getMessage();",
                        new ReferenceScopeTestStruct[]{
                                callee("Exception", "\\.", "foo()", dfault, 1, 1, 0, 0, 0),
                                method("\\.Exception.", "getMessage()", "\\.", "string?", 1, 1, 0, 1)
                        },
                        new TypeCheckStruct[]{
                                struct("fCall", EBuiltInType.Exception, 1, 1, 0, 0),
                                struct("mpCall", EBuiltInType.StringNullable, 1, 1, 0)
                        }
                },
                {"class A{function Exception foo(){return new Exception;}}"
                        + "A $a = new A; $a->foo()->getMessage();",
                        new ReferenceScopeTestStruct[]{
                                callee("A", "\\.\\.", "$a", dfault, 1, 2, 0, 0, 0),
                                method("\\.\\.A.", "foo()", "\\.", "Exception", 1, 2, 0, 0, 1),
                                method("\\.Exception.", "getMessage()", "\\.", "string?", 1, 2, 0, 1),
                        },
                        new TypeCheckStruct[]{
                                struct("mCall", EBuiltInType.Exception, 1, 2, 0, 0),
                                struct("mpCall", EBuiltInType.StringNullable, 1, 2, 0)
                        }
                },
                {"class A{static function Exception foo(){return new Exception;}}"
                        + "A::foo()->getMessage();",
                        new ReferenceScopeTestStruct[]{
                                method("\\.\\.A.", "foo()", "\\.", "Exception", 1, 1, 0, 0, 1),
                                method("\\.Exception.", "getMessage()", "\\.", "string?", 1, 1, 0, 1),
                        },
                        new TypeCheckStruct[]{
                                struct("smCall", EBuiltInType.Exception, 1, 1, 0, 0),
                                struct("mpCall", EBuiltInType.StringNullable, 1, 1, 0)
                        }
                }
        });
    }

    private static ReferenceScopeTestStruct callee(String type, String typeScope,
            String callee, String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct(callee, scope, Arrays.asList(accessToScope), type, typeScope);
    }

    private static ReferenceScopeTestStruct method(String classScope, String methodName,
            String returnTypeScope, String type, Integer... accessToScope) {
        return new ReferenceScopeTestStruct(methodName, classScope, Arrays.asList(accessToScope), type,
                returnTypeScope);
    }
}
