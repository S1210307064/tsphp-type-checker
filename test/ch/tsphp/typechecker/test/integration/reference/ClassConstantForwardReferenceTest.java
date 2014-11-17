/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ClassConstantForwardReferenceTest extends AReferenceTest
{


    public ClassConstantForwardReferenceTest(String testString) {
        super(testString);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verifyReferences() {
        //nothing to check, should just not cause an error
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {

        //class constants cannot have forward references

        return Arrays.asList(new Object[][]{
                {"class A{function void foo(){\n A::a;} const int \n a = 1;}"},
                {"class A{function void foo(){\n A::a; \n A::a;} const int \n a = 1;}"},
        });
    }

}
