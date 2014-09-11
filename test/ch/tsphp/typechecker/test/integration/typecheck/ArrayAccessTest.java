/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ArrayAccessTest extends AOperatorTypeCheckTest
{

    public ArrayAccessTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        String[][] types = new String[][]{{"bool", "true"}, {"int", "1"}, {"float", "1.0"}, {"string", "'hello'"}};
        for (String[] type : types) {
            collection.add(new Object[]{
                    "array $a = [1,2];" + type[0] + " $b = " + type[1] + "; $a[$b];",
                    new TypeCheckStruct[]{struct("arrAccess", Mixed, 1, 2, 0)}
            });
            collection.add(new Object[]{
                    "array $a = [1,2];" + type[0] + " $b=" + type[1] + "; $a = (array) $a[$b]; $a[$b];",
                    new TypeCheckStruct[]{struct("arrAccess", Mixed, 1, 3, 0)}
            });
        }
        return collection;
    }
}
