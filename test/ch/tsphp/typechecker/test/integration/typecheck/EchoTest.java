/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RunWith(Parameterized.class)
public class EchoTest extends AOperatorTypeCheckTest
{

    public EchoTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"echo true;", castTypeStruct(0, "true", Bool)},
                {"echo false;", castTypeStruct(0, "false", Bool)},
                {"echo 1;", castTypeStruct(0, "1", Int)},
                {"echo 1.4;", castTypeStruct(0, "1.4", Float)},
                {"echo 'hello';", typeStruct("'hello'", String, 1, 0, 0)},
                {"echo \"hello\";", typeStruct("\"hello\"", String, 1, 0, 0)},
                {"const bool b=false; echo b;", castTypeStruct(1, "b#", Bool)},
                {"const int b=1; echo b;", castTypeStruct(1, "b#", Int)},
                {"const float b=1.0; echo b;", castTypeStruct(1, "b#", Float)},
                {"const string b='hello'; echo b;", typeStruct("b#", String, 1, 1, 0)},
                {"bool $b = false; echo $b;", castTypeStruct(1, "$b", Bool)},
                {"int $b = 1; echo $b;", castTypeStruct(1, "$b", Int)},
                {"float $b = 1; echo $b;", castTypeStruct(1, "$b", Float)},
                {"string $b = 1; echo $b;", typeStruct("$b", String, 1, 1, 0)},
                //TODO rstoll TSPHP-829 echo and castings
//                {"echo true, false, 1, 1.0, \"hello\";", castTypeStruct(
//                        castTypeStruct(0, "true", Bool),
//                        castTypeStruct(0, 1, "false", Bool),
//                        castTypeStruct(0, 2, "1", Int),
//                        castTypeStruct(0, 3, "1.4", Float),
//                        typeStruct("\"hello\"", String, 1, 0, 4)
//                )}
        });
    }

    private static TypeCheckStruct[] castTypeStruct(TypeCheckStruct[]... typeCheckStructs) {
        List<TypeCheckStruct> list = new ArrayList<>();
        for (TypeCheckStruct[] typeCheckStructs1 : typeCheckStructs) {
            Collections.addAll(list, typeCheckStructs1);
        }
        return list.toArray(new TypeCheckStruct[list.size()]);
    }

    private static TypeCheckStruct[] castTypeStruct(int echoPos, String expression, EBuiltInType type) {
        return castTypeStruct(echoPos, 0, expression, type);
    }

    private static TypeCheckStruct[] castTypeStruct(
            int echoPos, int posInsideEcho, String expression, EBuiltInType type) {
        return new TypeCheckStruct[]{
                struct("casting", String, 1, echoPos, posInsideEcho),
                struct("string", String, 1, echoPos, posInsideEcho, 0, 1),
                struct(expression, type, 1, echoPos, posInsideEcho, 1)
        };
    }
}
