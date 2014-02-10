package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class IfTest extends AOperatorTypeCheckTest
{

    public IfTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"if(true);", new TypeCheckStruct[]{struct("true", Bool, 1, 0, 0)}},
                {"if(false);", new TypeCheckStruct[]{struct("false", Bool, 1, 0, 0)}},
                {"if(true);else if(false);", new TypeCheckStruct[]{
                        struct("true", Bool, 1, 0, 0),
                        struct("false", Bool, 1, 0, 2, 0, 0)
                }
                },
                {"if(false);else if(true);", new TypeCheckStruct[]{
                        struct("false", Bool, 1, 0, 0),
                        struct("true", Bool, 1, 0, 2, 0, 0)
                }
                }
        });
    }
}
