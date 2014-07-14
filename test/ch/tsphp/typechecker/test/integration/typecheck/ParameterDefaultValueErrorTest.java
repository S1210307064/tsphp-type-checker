/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;

@RunWith(Parameterized.class)
public class ParameterDefaultValueErrorTest extends ATypeCheckErrorTest
{

    public ParameterDefaultValueErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
        Mockito.verify(typeCheckErrorReporter).wrongAssignment(
                any(ITSPHPAst.class), any(ITSPHPAst.class), any(ITSPHPAst.class));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("class A{", "}"));
        return collection;
    }

    private static Collection<Object[]> getVariations(String prefix, String appendix) {
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("=", 2, 1)};
        return Arrays.asList(new Object[][]{
                //see TSPHP-757 type of default value does not match - causes NullPointerException
                //should not cause a NullPointerException. Yet, a type check error should be produced
                {prefix + "function void foo(bool \n $a = 1){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = +2){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = -3){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = [1]){}" + appendix, errorDto},
                {prefix + "function void foo(bool \n $a = null){}" + appendix, errorDto},
                {"const int V = 1;" + prefix + "function void foo(bool \n $a = V){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(bool \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(bool \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(bool \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {"class A{const int V = 1;}" + prefix + "function void foo(bool \n $a = A::V){}" + appendix,
// errorDto},
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(bool \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(bool \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(bool \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
                {prefix + "function void foo(bool? \n $a = 1){}" + appendix, errorDto},
                {prefix + "function void foo(bool? \n $a = +2){}" + appendix, errorDto},
                {prefix + "function void foo(bool? \n $a = -3){}" + appendix, errorDto},
                {prefix + "function void foo(bool? \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(bool? \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(bool? \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(bool? \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(bool? \n $a = [1]){}" + appendix, errorDto},
                {"const int V = 1;" + prefix + "function void foo(bool? \n $a = V){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(bool? \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(bool? \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(bool? \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {"class A{const int V = 1;}" + prefix + "function void foo(bool? \n $a = A::V){}" + appendix,
// errorDto},
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(bool? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(bool? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(bool? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
                {prefix + "function void foo(int \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(int \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(int \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(int \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(int \n $a = [1]){}" + appendix, errorDto},
                {prefix + "function void foo(int \n $a = null){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(int \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(int \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(int \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(int \n $a = A::V){}" + appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(int \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(int \n $a = A::V){}" + appendix,
//                        errorDto
//                },
                {prefix + "function void foo(int? \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(int? \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(int? \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(int? \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(int? \n $a = [1]){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(int? \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(int? \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(int? \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(int? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(int? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(int? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
                {prefix + "function void foo(float \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(float \n $a = [1]){}" + appendix, errorDto},
                {prefix + "function void foo(float \n $a = null){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(float \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(float \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(float \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(float \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
                {prefix + "function void foo(float? \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(float? \n $a = [1]){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(float? \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(float? \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {
//                        "class A{const string V = 'a';}" + prefix
//                                + "function void foo(float? \n $a = A::V){}" + appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix
//                                + "function void foo(float? \n $a = A::V){}" + appendix,
//                        errorDto
//                },
                {prefix + "function void foo(string \n $a = [1]){}" + appendix, errorDto},
                {prefix + "function void foo(string \n $a = null){}" + appendix, errorDto},
                {prefix + "function void foo(string? \n $a = [1]){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(bool \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(bool \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },

                {prefix + "function void foo(array \n $a = true){}" + appendix, errorDto},
                {prefix + "function void foo(array \n $a = 1){}" + appendix, errorDto},
                {prefix + "function void foo(array \n $a = +2){}" + appendix, errorDto},
                {prefix + "function void foo(array \n $a = -3){}" + appendix, errorDto},
                {prefix + "function void foo(array \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(array \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(array \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(array \n $a = 'a'){}" + appendix, errorDto},
                //TODO TSPHP-794 - null as default value even though param not defined as nullable
                //{prefix + "function void foo(array \n $a = null){}" + appendix, errorDto},
                {"const int V = 1;" + prefix + "function void foo(array \n $a = V){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(array \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(array \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(array \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {"class A{const int V = 1;}" + prefix + "function void foo(array \n $a = A::V){}" + appendix,
// errorDto},
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(array \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(array \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(array \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
                {prefix + "function void foo(array? \n $a = true){}" + appendix, errorDto},
                {prefix + "function void foo(array? \n $a = 1){}" + appendix, errorDto},
                {prefix + "function void foo(array? \n $a = +2){}" + appendix, errorDto},
                {prefix + "function void foo(array? \n $a = -3){}" + appendix, errorDto},
                {prefix + "function void foo(array? \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(array? \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(array? \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(array? \n $a = 'a'){}" + appendix, errorDto},
                {"const int V = 1;" + prefix + "function void foo(array? \n $a = V){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(array? \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(array? \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(array? \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {"class A{const int V = 1;}" + prefix + "function void foo(array? \n $a = A::V){}" + appendix,
// errorDto},
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(array? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(array? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(array? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },

                {prefix + "function void foo(Exception \n $a = true){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = 1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = +2){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = -3){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(Exception \n $a = [1]){}" + appendix, errorDto},
                //TODO TSPHP-794 - null as default value even though param not defined as nullable
                //{prefix + "function void foo(Exception \n $a = null){}" + appendix, errorDto},
                {"const int V = 1;" + prefix + "function void foo(Exception \n $a = V){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(Exception \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(Exception \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                //{"const array V = [1];" + prefix + "function void foo(Exception \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {"class A{const int V = 1;}" + prefix + "function void foo(Exception \n $a = A::V){}" + appendix,
// errorDto},
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(Exception \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(Exception \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(Exception \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
                {prefix + "function void foo(Exception? \n $a = true){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = 1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = +2){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = -3){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = 1.1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = +2.1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = -3.1){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = 'a'){}" + appendix, errorDto},
                {prefix + "function void foo(Exception? \n $a = [1]){}" + appendix, errorDto},
                {"const int V = 1;" + prefix + "function void foo(Exception? \n $a = V){}" + appendix, errorDto},
                {"const float V = 1.1;" + prefix + "function void foo(Exception? \n $a = V){}" + appendix, errorDto},
                {"const string V = 'a';" + prefix + "function void foo(Exception? \n $a = V){}" + appendix, errorDto},
                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
                // {"const array V = [1];" + prefix + "function void foo(Exception? \n $a = V){}" + appendix, errorDto},
                //TODO TSPHP-792 - class constant as default value
//                {"class A{const int V = 1;}" + prefix + "function void foo(Exception? \n $a = A::V){}" + appendix,
// errorDto},
//                {
//                        "class A{const float V = 1.1;}" + prefix + "function void foo(Exception? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
//                {
//                        "class A{const string V = 'a';}" + prefix + "function void foo(Exception? \n $a = A::V){}"
// + appendix,
//                        errorDto
//                },
//                //Constant arrays are not possible yet - uncomment as soon as they are supported ;)
//                {
//                        "class A{const array V = [1];}" + prefix + "function void foo(Exception? \n $a = A::V){}" +
// appendix,
//                        errorDto
//                },
        });
    }

    @Override
    protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
        return spy(super.createTypeCheckerErrorReporter());
    }
}

