/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.testutils.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Null;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ConstantInitialValueHelper
{

    public static Collection<Object[]> errorTestStrings(String prefix, String appendix, String identifier,
            boolean isNotConstant, boolean isParameter) {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("=", 2, 1)};

        collection.addAll(Arrays.asList(new Object[][]{
            {
                prefix + "bool\n " + identifier + " = 1" + appendix,
                errorDto
            },
            {
                prefix + "bool\n " + identifier + " = 1.0" + appendix,
                errorDto
            },
            {
                prefix + "bool\n " + identifier + " = 'hello'" + appendix,
                errorDto
            },
            {
                prefix + "bool\n " + identifier + " = [1,2]" + appendix,
                errorDto
            },
            {
                prefix + "bool\n " + identifier + " = null" + appendix,
                errorDto
            },
            {
                prefix + "int\n " + identifier + " = 1.0" + appendix,
                errorDto
            },
            {
                prefix + "int\n " + identifier + " = 'hello'" + appendix,
                errorDto
            },
            {
                prefix + "int\n " + identifier + " = [1,2]" + appendix,
                errorDto
            },
            {
                prefix + "int\n " + identifier + " = null" + appendix,
                errorDto
            },
            {
                prefix + "float\n " + identifier + " = 'hello'" + appendix,
                errorDto
            },
            {
                prefix + "float\n " + identifier + " = [0,1]" + appendix,
                errorDto
            },
            {
                prefix + "float\n " + identifier + " = null" + appendix,
                errorDto
            },
            {
                prefix + "string\n " + identifier + " = [0,1]" + appendix,
                errorDto
            },
            {
                prefix + "string\n " + identifier + " = null" + appendix,
                errorDto
            }
        }));
        if (isNotConstant) {
            collection.addAll(Arrays.asList(new Object[][]{
                {
                    prefix + "bool?\n " + identifier + " = 1" + appendix,
                    errorDto
                },
                {
                    prefix + "bool?\n " + identifier + " = 1.0" + appendix,
                    errorDto
                },
                {
                    prefix + "bool?\n " + identifier + " = 'hello'" + appendix,
                    errorDto
                },
                {
                    prefix + "bool?\n " + identifier + " = [1,2]" + appendix,
                    errorDto
                },
                {
                    prefix + "int?\n " + identifier + " = 1.0" + appendix,
                    errorDto
                },
                {
                    prefix + "int?\n " + identifier + " = 'hello'" + appendix,
                    errorDto
                },
                {
                    prefix + "int?\n " + identifier + " = [1,2]" + appendix,
                    errorDto
                },
                {
                    prefix + "float?\n " + identifier + " = 'hello'" + appendix,
                    errorDto
                },
                {
                    prefix + "string?\n " + identifier + " = [0,1]" + appendix,
                    errorDto
                },
                {
                    prefix + "array\n " + identifier + " = true" + appendix,
                    errorDto
                }, {
                    prefix + "array\n " + identifier + " = 1" + appendix,
                    errorDto
                },
                {
                    prefix + "array\n " + identifier + " = 1.0" + appendix,
                    errorDto
                }, {
                    prefix + "array\n " + identifier + " = 'hello'" + appendix,
                    errorDto
                },
                {
                    prefix + "resource\n " + identifier + " = true" + appendix,
                    errorDto
                },
                {
                    prefix + "resource\n " + identifier + " = 1" + appendix,
                    errorDto
                },
                {
                    prefix + "resource\n " + identifier + " = 1.0" + appendix,
                    errorDto
                },
                {
                    prefix + "resource\n " + identifier + " = 'hello'" + appendix,
                    errorDto
                },
                {
                    prefix + "resource\n " + identifier + " = [1,2]" + appendix,
                    errorDto
                },}));
        }

        return collection;
    }

    public static Collection<Object[]> testStrings(String prefix, String appendix, String identifier,
            boolean isNotConstant, boolean isParameter, Integer... accessOrder) {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(Arrays.asList(new Object[][]{
            {
                prefix + "bool " + identifier + " = false" + appendix,
                new TypeCheckStruct[]{struct("false", Bool, accessOrder)}
            },
            {
                prefix + "bool " + identifier + " = true" + appendix,
                new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
            },
            {
                prefix + "int " + identifier + " = true" + appendix,
                new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
            },
            {
                prefix + "int " + identifier + " = 1" + appendix,
                new TypeCheckStruct[]{struct("1", Int, accessOrder)}
            },
            {
                prefix + "float " + identifier + " = true" + appendix,
                new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
            },
            {
                prefix + "float " + identifier + " = 6" + appendix,
                new TypeCheckStruct[]{struct("6", Int, accessOrder)}
            },
            {
                prefix + "float " + identifier + " = 2.56" + appendix,
                new TypeCheckStruct[]{struct("2.56", Float, accessOrder)}
            },
            {
                prefix + "string " + identifier + " = true" + appendix,
                new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
            },
            {
                prefix + "string " + identifier + " = 1" + appendix,
                new TypeCheckStruct[]{struct("1", Int, accessOrder)}
            },
            {
                prefix + "string " + identifier + " = 5.6" + appendix,
                new TypeCheckStruct[]{struct("5.6", Float, accessOrder)}
            },
            {
                prefix + "string " + identifier + " = 'hello'" + appendix,
                new TypeCheckStruct[]{struct("'hello'", String, accessOrder)}
            },
            {
                prefix + "string " + identifier + " = \"yellow\"" + appendix,
                new TypeCheckStruct[]{struct("\"yellow\"", String, accessOrder)}
            }
        }));
        if (isNotConstant) {
            collection.addAll(Arrays.asList(new Object[][]{
                {
                    prefix + "bool? " + identifier + " = false" + appendix,
                    new TypeCheckStruct[]{struct("false", Bool, accessOrder)}
                },
                {
                    prefix + "bool? " + identifier + " = true" + appendix,
                    new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
                },
                {
                    prefix + "bool? " + identifier + " = null" + appendix,
                    new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                },
                {
                    prefix + "int? " + identifier + " = true" + appendix,
                    new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
                },
                {
                    prefix + "int? " + identifier + " = 1" + appendix,
                    new TypeCheckStruct[]{struct("1", Int, accessOrder)}
                },
                {
                    prefix + "int? " + identifier + " = null" + appendix,
                    new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                },
                {
                    prefix + "float? " + identifier + " = true" + appendix,
                    new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
                },
                {
                    prefix + "float? " + identifier + " = 6" + appendix,
                    new TypeCheckStruct[]{struct("6", Int, accessOrder)}
                },
                {
                    prefix + "float? " + identifier + " = 2.56" + appendix,
                    new TypeCheckStruct[]{struct("2.56", Float, accessOrder)}
                },
                {
                    prefix + "float? " + identifier + " = null" + appendix,
                    new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                },
                {
                    prefix + "string? " + identifier + " = true" + appendix,
                    new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
                },
                {
                    prefix + "string? " + identifier + " = 1" + appendix,
                    new TypeCheckStruct[]{struct("1", Int, accessOrder)}
                },
                {
                    prefix + "string? " + identifier + " = 5.6" + appendix,
                    new TypeCheckStruct[]{struct("5.6", Float, accessOrder)}
                },
                {
                    prefix + "string? " + identifier + " = 'hello'" + appendix,
                    new TypeCheckStruct[]{struct("'hello'", String, accessOrder)}
                },
                {
                    prefix + "string? " + identifier + " = \"yellow\"" + appendix,
                    new TypeCheckStruct[]{struct("\"yellow\"", String, accessOrder)}
                },
                {
                    prefix + "string? " + identifier + " = null" + appendix,
                    new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                },
                {
                    prefix + "array " + identifier + " = [0]" + appendix,
                    new TypeCheckStruct[]{struct("array", Array, accessOrder)}
                },
                {
                    prefix + "array " + identifier + " = array(1,2)" + appendix,
                    new TypeCheckStruct[]{struct("array", Array, accessOrder)}
                },
                {
                    prefix + "object " + identifier + " = true" + appendix,
                    new TypeCheckStruct[]{struct("true", Bool, accessOrder)}
                },
                {
                    prefix + "object " + identifier + " = false" + appendix,
                    new TypeCheckStruct[]{struct("false", Bool, accessOrder)}
                },
                {
                    prefix + "object " + identifier + " = 1" + appendix,
                    new TypeCheckStruct[]{struct("1", Int, accessOrder)}
                },
                {
                    prefix + "object " + identifier + " = 1.0" + appendix,
                    new TypeCheckStruct[]{struct("1.0", Float, accessOrder)}
                },
                {
                    prefix + "object " + identifier + " = 'hello'" + appendix,
                    new TypeCheckStruct[]{struct("'hello'", String, accessOrder)}
                },
                {
                    prefix + "object " + identifier + " = [1,2]" + appendix,
                    new TypeCheckStruct[]{struct("array", Array, accessOrder)}
                }
            }));
            if (isParameter) {
                collection.addAll(Arrays.asList(new Object[][]{
                    {
                        prefix + "array? " + identifier + " = null" + appendix,
                        new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                    },
                    {
                        prefix + "resource? " + identifier + " = null" + appendix,
                        new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                    },
                    {
                        prefix + "object? " + identifier + " = null" + appendix,
                        new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                    },
                    {
                        prefix + "ErrorException? " + identifier + " = null" + appendix,
                        new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                    },
                    {
                        prefix + "Exception? " + identifier + " = null" + appendix,
                        new TypeCheckStruct[]{struct("null", Null, accessOrder)}
                    }
                }));
            }
        }
        return collection;

    }

    private static TypeCheckStruct struct(String astText, EBuiltInType type, Integer... accessOrder) {
        Integer[] ints = Arrays.copyOf(accessOrder, accessOrder.length + 3);
        ints[accessOrder.length] = 0;
        ints[accessOrder.length + 1] = 1;
        ints[accessOrder.length + 2] = 0;
        return AOperatorTypeCheckTest.struct(astText, type, ints);
    }
}
