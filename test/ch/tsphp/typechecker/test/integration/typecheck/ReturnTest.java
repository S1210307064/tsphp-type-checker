/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ReturnTest extends AOperatorTypeCheckTest
{

    public ReturnTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.add(new Object[]{"function void a(){return;}", new TypeCheckStruct[]{}});
        collection.add(new Object[]{"class A{function void a(){return;}}", new TypeCheckStruct[]{}});


        Object[][] types = TypeHelper.getAllTypesInclTokenAndDefaultValue();

        for (Object[] type : types) {
            if (type[0].equals("void")) {
                continue;
            }
            collection.add(new Object[]{
                    "function " + type[0] + " a(){" + type[0] + " $b=" + type[2] + "; return $b;}",
                    new TypeCheckStruct[]{struct("return", (EBuiltInType) type[1], 1, 0, 4, 1)}
            });
            collection.add(new Object[]{
                    "class A{function " + type[0] + " a(){" + type[0] + " $b=" + type[2] + "; return $b;}}",
                    new TypeCheckStruct[]{struct("return", (EBuiltInType) type[1], 1, 0, 4, 0, 4, 1)}
            });
        }

        return collection;
    }
}
