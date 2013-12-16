package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CloneTest extends AOperatorTypeCheckTest
{

    public CloneTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"ErrorException $a=null; clone $a;", new TypeCheckStruct[]{struct("clone", ErrorException, 1, 1, 0)}},
                {"Exception $a=null; clone $a;", new TypeCheckStruct[]{struct("clone", Exception, 1, 1, 0)}},
                {"object $a=null; clone (Exception) $a;", new TypeCheckStruct[]{struct("clone", Exception, 1, 1, 0)}},
        });
    }
}
