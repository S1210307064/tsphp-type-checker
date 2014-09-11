/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class EqualityOperatorTest extends AOperatorTypeCheckTest
{

    public EqualityOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String[][] typesInclDefaultValue = TypeHelper.getAllTypesInclDefaultValue();
        String[] operators = new String[]{"==", "!="};
        for (String operator : operators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"true " + operator + " true;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"true " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"false " + operator + " true;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"false " + operator + " false;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"1 " + operator + " 2;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"2.0 " + operator + " 2.3;", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"'hi' " + operator + " 'hello';", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"'hi' " + operator + " \"hello\";", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"\"hi\" " + operator + " 'hello';", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"\"hi\" " + operator + " \"hello\";", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"[1,2] " + operator + " [7,8];", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"[1,2] " + operator + " array(7,8);", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"array(1,2) " + operator + " [7,8];", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
                    {"array(1,2) " + operator + " array(7,8);", new TypeCheckStruct[]{struct(operator, Bool, 1, 0, 0)}},
            }));
            for (String[] type : typesInclDefaultValue) {
                collection.add(new Object[]{
                        type[0] + " $a=" + type[1] + ";" + type[0] + " $b=" + type[1] + "; $a " + operator + " $b;",
                        typeStruct(operator, Bool, 1, 2, 0)
                });
            }
        }
        return collection;
    }
}
