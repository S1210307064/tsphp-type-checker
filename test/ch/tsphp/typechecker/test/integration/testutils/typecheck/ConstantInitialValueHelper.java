package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.error.ReferenceErrorDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ConstantInitialValueHelper
{

    public static Collection<Object[]> errorTestStrings(String prefix, String appendix, String identifier,
            boolean isNotConstant, boolean isParameter) {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("=", 2, 1)};
        ReferenceErrorDto[] errorDtoExpression = new ReferenceErrorDto[]{new ReferenceErrorDto("$a", 2, 1)};

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
                },
                //no expressions
                {
                        prefix + "int\n " + identifier + " = 60*60" + appendix,
                        errorDtoExpression
                },
                {
                        prefix + "string\n " + identifier + " = 'hello '.' robert'" + appendix,
                        errorDtoExpression
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
                    },
                    //no expressions
                    {
                            prefix + "ErrorException\n " + identifier + " = new ErrorException()" + appendix,
                            errorDtoExpression
                    },
                    {
                            prefix + "Exception\n " + identifier + " = new Exception()" + appendix,
                            errorDtoExpression
                    },
                    {
                            prefix + "Exception\n " + identifier + " = new ErrorException()" + appendix,
                            errorDtoExpression
                    },
                    {
                            prefix + "object\n " + identifier + " = new Exception()" + appendix,
                            errorDtoExpression
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
                        new TypeCheckStruct[]{struct("false", AOperatorTypeCheckTest.Bool, accessOrder)}
                },
                {
                        prefix + "bool " + identifier + " = true" + appendix,
                        new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                },
                {
                        prefix + "int " + identifier + " = true" + appendix,
                        new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                },
                {
                        prefix + "int " + identifier + " = 1" + appendix,
                        new TypeCheckStruct[]{struct("1", AOperatorTypeCheckTest.Int, accessOrder)}
                },
                {
                        prefix + "float " + identifier + " = true" + appendix,
                        new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                },
                {
                        prefix + "float " + identifier + " = 6" + appendix,
                        new TypeCheckStruct[]{struct("6", AOperatorTypeCheckTest.Int, accessOrder)}
                },
                {
                        prefix + "float " + identifier + " = 2.56" + appendix,
                        new TypeCheckStruct[]{struct("2.56", AOperatorTypeCheckTest.Float, accessOrder)}
                },
                {
                        prefix + "string " + identifier + " = true" + appendix,
                        new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                },
                {
                        prefix + "string " + identifier + " = 1" + appendix,
                        new TypeCheckStruct[]{struct("1", AOperatorTypeCheckTest.Int, accessOrder)}
                },
                {
                        prefix + "string " + identifier + " = 5.6" + appendix,
                        new TypeCheckStruct[]{struct("5.6", AOperatorTypeCheckTest.Float, accessOrder)}
                },
                {
                        prefix + "string " + identifier + " = 'hello'" + appendix,
                        new TypeCheckStruct[]{struct("'hello'", AOperatorTypeCheckTest.String, accessOrder)}
                },
                {
                        prefix + "string " + identifier + " = \"yellow\"" + appendix,
                        new TypeCheckStruct[]{struct("\"yellow\"", AOperatorTypeCheckTest.String, accessOrder)}
                }
        }));
        if (isNotConstant) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {
                            prefix + "bool? " + identifier + " = false" + appendix,
                            new TypeCheckStruct[]{struct("false", AOperatorTypeCheckTest.Bool, accessOrder)}
                    },
                    {
                            prefix + "bool? " + identifier + " = true" + appendix,
                            new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                    },
                    {
                            prefix + "bool? " + identifier + " = null" + appendix,
                            new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                    },
                    {
                            prefix + "int? " + identifier + " = true" + appendix,
                            new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                    },
                    {
                            prefix + "int? " + identifier + " = 1" + appendix,
                            new TypeCheckStruct[]{struct("1", AOperatorTypeCheckTest.Int, accessOrder)}
                    },
                    {
                            prefix + "int? " + identifier + " = null" + appendix,
                            new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                    },
                    {
                            prefix + "float? " + identifier + " = true" + appendix,
                            new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                    },
                    {
                            prefix + "float? " + identifier + " = 6" + appendix,
                            new TypeCheckStruct[]{struct("6", AOperatorTypeCheckTest.Int, accessOrder)}
                    },
                    {
                            prefix + "float? " + identifier + " = 2.56" + appendix,
                            new TypeCheckStruct[]{struct("2.56", AOperatorTypeCheckTest.Float, accessOrder)}
                    },
                    {
                            prefix + "float? " + identifier + " = null" + appendix,
                            new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                    },
                    {
                            prefix + "string? " + identifier + " = true" + appendix,
                            new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                    },
                    {
                            prefix + "string? " + identifier + " = 1" + appendix,
                            new TypeCheckStruct[]{struct("1", AOperatorTypeCheckTest.Int, accessOrder)}
                    },
                    {
                            prefix + "string? " + identifier + " = 5.6" + appendix,
                            new TypeCheckStruct[]{struct("5.6", AOperatorTypeCheckTest.Float, accessOrder)}
                    },
                    {
                            prefix + "string? " + identifier + " = 'hello'" + appendix,
                            new TypeCheckStruct[]{struct("'hello'", AOperatorTypeCheckTest.String, accessOrder)}
                    },
                    {
                            prefix + "string? " + identifier + " = \"yellow\"" + appendix,
                            new TypeCheckStruct[]{struct("\"yellow\"", AOperatorTypeCheckTest.String, accessOrder)}
                    },
                    {
                            prefix + "string? " + identifier + " = null" + appendix,
                            new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                    },
                    {
                            prefix + "array " + identifier + " = [0]" + appendix,
                            new TypeCheckStruct[]{struct("array", AOperatorTypeCheckTest.Array, accessOrder)}
                    },
                    {
                            prefix + "array " + identifier + " = array(1,2)" + appendix,
                            new TypeCheckStruct[]{struct("array", AOperatorTypeCheckTest.Array, accessOrder)}
                    },
                    {
                            prefix + "object " + identifier + " = true" + appendix,
                            new TypeCheckStruct[]{struct("true", AOperatorTypeCheckTest.Bool, accessOrder)}
                    },
                    {
                            prefix + "object " + identifier + " = false" + appendix,
                            new TypeCheckStruct[]{struct("false", AOperatorTypeCheckTest.Bool, accessOrder)}
                    },
                    {
                            prefix + "object " + identifier + " = 1" + appendix,
                            new TypeCheckStruct[]{struct("1", AOperatorTypeCheckTest.Int, accessOrder)}
                    },
                    {
                            prefix + "object " + identifier + " = 1.0" + appendix,
                            new TypeCheckStruct[]{struct("1.0", AOperatorTypeCheckTest.Float, accessOrder)}
                    },
                    {
                            prefix + "object " + identifier + " = 'hello'" + appendix,
                            new TypeCheckStruct[]{struct("'hello'", AOperatorTypeCheckTest.String, accessOrder)}
                    },
                    {
                            prefix + "object " + identifier + " = [1,2]" + appendix,
                            new TypeCheckStruct[]{struct("array", AOperatorTypeCheckTest.Array, accessOrder)}
                    }
            }));
            if (isParameter) {
                collection.addAll(Arrays.asList(new Object[][]{
                        {
                                prefix + "array? " + identifier + " = null" + appendix,
                                new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                        },
                        {
                                prefix + "resource? " + identifier + " = null" + appendix,
                                new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                        },
                        {
                                prefix + "object? " + identifier + " = null" + appendix,
                                new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                        },
                        {
                                prefix + "ErrorException? " + identifier + " = null" + appendix,
                                new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
                        },
                        {
                                prefix + "Exception? " + identifier + " = null" + appendix,
                                new TypeCheckStruct[]{struct("null", AOperatorTypeCheckTest.Null, accessOrder)}
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
