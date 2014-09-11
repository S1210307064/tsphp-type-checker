/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.Null;
import static ch.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest.String;

public class InitialValueHelper
{
    public static Collection<Object[]> testStrings(String prefix, String appendix, String id,
            boolean isNotConstant, boolean isParameter, Integer... accessOrder) {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "bool " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                {prefix + "bool " + id + " = true" + appendix, typeStruct("true", Bool, accessOrder)},
                {prefix + "int " + id + " = 1" + appendix, typeStruct("1", Int, accessOrder)},
                {prefix + "float " + id + " = 2.56" + appendix, typeStruct("2.56", Float, accessOrder)},
                {prefix + "string " + id + " = 'hello'" + appendix, typeStruct("'hello'", String, accessOrder)},
                {prefix + "string " + id + " = \"yellow\"" + appendix, typeStruct("\"yellow\"", String, accessOrder)}
        }));
        if (isNotConstant) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {prefix + "bool! " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "bool! " + id + " = true" + appendix, typeStruct("true", Bool, accessOrder)},
                    {prefix + "bool? " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "bool? " + id + " = true" + appendix, typeStruct("true", Bool, accessOrder)},
                    {prefix + "bool? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "bool!? " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "bool!? " + id + " = true" + appendix, typeStruct("true", Bool, accessOrder)},
                    {prefix + "bool!? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "int! " + id + " = 1" + appendix, typeStruct("1", Int, accessOrder)},
                    {prefix + "int! " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "int? " + id + " = 1" + appendix, typeStruct("1", Int, accessOrder)},
                    {prefix + "int? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "int!? " + id + " = 1" + appendix, typeStruct("1", Int, accessOrder)},
                    {prefix + "int!? " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "int!? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "float! " + id + " = 2.56" + appendix, typeStruct("2.56", Float, accessOrder)},
                    {prefix + "float! " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "float? " + id + " = 2.56" + appendix, typeStruct("2.56", Float, accessOrder)},
                    {prefix + "float? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "float!? " + id + " = 2.56" + appendix, typeStruct("2.56", Float, accessOrder)},
                    {prefix + "float!? " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "float!? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "string! " + id + " = 'hello'" + appendix, typeStruct("'hello'", String, accessOrder)},
                    {prefix + "string! " + id + " = \"hi\"" + appendix, typeStruct("\"hi\"", String, accessOrder)},
                    {prefix + "string! " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "string? " + id + " = 'hello'" + appendix, typeStruct("'hello'", String, accessOrder)},
                    {prefix + "string? " + id + " = \"hi\"" + appendix, typeStruct("\"hi\"", String, accessOrder)},
                    {prefix + "string? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "string!? " + id + " = 'hello'" + appendix, typeStruct("'hello'", String, accessOrder)},
                    {prefix + "string!? " + id + " = \"hi\"" + appendix, typeStruct("\"hi\"", String, accessOrder)},
                    {prefix + "string!? " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "string!? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "array " + id + " = [0]" + appendix, typeStruct("array", Array, accessOrder)},
                    {prefix + "array " + id + " = array(1,2)" + appendix, typeStruct("array", Array, accessOrder)},
                    {prefix + "array! " + id + " = [0]" + appendix, typeStruct("array", Array, accessOrder)},
                    {prefix + "array! " + id + " = array(1,2)" + appendix, typeStruct("array", Array, accessOrder)},
                    {prefix + "array! " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "resource " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "resource! " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "resource! " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "mixed " + id + " = true" + appendix, typeStruct("true", Bool, accessOrder)},
                    {prefix + "mixed " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                    {prefix + "mixed " + id + " = 1" + appendix, typeStruct("1", Int, accessOrder)},
                    {prefix + "mixed " + id + " = 1.0" + appendix, typeStruct("1.0", Float, accessOrder)},
                    {prefix + "mixed " + id + " = 'hello'" + appendix, typeStruct("'hello'", String, accessOrder)},
                    {prefix + "mixed " + id + " = [1,2]" + appendix, typeStruct("array", Array, accessOrder)},
                    {prefix + "mixed " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "Exception " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "Exception! " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "ErrorException " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                    {prefix + "ErrorException! " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
            }));
            if (isParameter) {
                collection.addAll(Arrays.asList(new Object[][]{
                        {prefix + "array? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                        {prefix + "array!? " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                        {prefix + "array!? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                        {prefix + "resource? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                        {prefix + "resource!? " + id + " = false" + appendix, typeStruct("false", Bool, accessOrder)},
                        {prefix + "resource!? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                        {prefix + "mixed? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                        {prefix + "Exception? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                        {prefix + "Exception!? " + id + " = null" + appendix, typeStruct("null", Null, accessOrder)},
                        {
                                prefix + "ErrorException? " + id + " = null" + appendix,
                                typeStruct("null", Null, accessOrder)
                        },
                        {
                                prefix + "ErrorException!? " + id + " = null" + appendix,
                                typeStruct("null", Null, accessOrder)
                        },
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

    private static TypeCheckStruct[] typeStruct(String astText, EBuiltInType type, Integer... accessOrder) {
        return new TypeCheckStruct[]{struct(astText, type, accessOrder)};
    }
}
