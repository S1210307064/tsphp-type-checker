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
package ch.tutteli.tsphp.typechecker.test.typecheck;

import ch.tutteli.tsphp.typechecker.test.testutils.reference.ReferenceScopeTestStruct;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.ErrorException;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Exception;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.FloatNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.IntNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Void;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AReferenceScopeTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.TypeCheckStruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@RunWith(Parameterized.class)
public class FunctionCallTest extends AReferenceScopeTypeCheckTest
{

    public FunctionCallTest(String testString,
            ReferenceScopeTestStruct[] scopeTestStructs, TypeCheckStruct[] typeCheckStructs) {
        super(testString, scopeTestStructs, typeCheckStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        Object[][] types = new Object[][]{
            {"bool", Bool},
            {"int", Int},
            {"float", Float},
            {"string", String},
            {"bool?", BoolNullable},
            {"int?", IntNullable},
            {"float?", FloatNullable},
            {"string?", StringNullable},
            {"array", Array},
            {"resource", Resource},
            {"object", Object},
            {"\\Exception", Exception},
            {"\\ErrorException", ErrorException},
            {"void", Void}
        };

        for (Object[] type : types) {
            String returnTypeString = (String) type[0];
            if (returnTypeString.charAt(0) == '\\') {
                returnTypeString = returnTypeString.substring(1);
            }
            EBuiltInType returnType = (EBuiltInType) type[1];
            collection.addAll(Arrays.asList(new Object[][]{
                {
                    "function " + type[0] + " foo(){} foo();",
                    scopeStructDefault(returnTypeString, "", 1, 1, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0)
                },
                {
                    "namespace{function " + type[0] + " foo(){} foo();}",
                    scopeStructDefault(returnTypeString, "", 1, 1, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0)
                },
                {
                    "namespace{function " + type[0] + " foo(){}} namespace{ foo();}",
                    scopeStructDefault(returnTypeString, "", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace{ foo();} namespace{function " + type[0] + " foo(){}} ",
                    scopeStructDefault(returnTypeString, "", 0, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 0, 1, 0, 0)
                },
                {
                    "namespace a{function " + type[0] + " foo(){}} namespace a{ foo();}",
                    scopeStruct(returnTypeString, "", "\\a\\.\\a\\.", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace a{ foo();} namespace a{function " + type[0] + " foo(){}} ",
                    scopeStruct(returnTypeString, "", "\\a\\.\\a\\.", 0, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 0, 1, 0, 0)
                },
                //absolute path
                {
                    "namespace{function " + type[0] + " foo(){}} namespace a{ \\foo();}",
                    scopeStructDefault(returnTypeString, "\\", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace a\\b{function " + type[0] + " foo(){}} namespace x{ \\a\\b\\foo();}",
                    scopeStruct(returnTypeString, "\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                },
                //relative
                {
                    "namespace a\\b{function " + type[0] + " foo(){}} namespace a{ b\\foo();}",
                    scopeStruct(returnTypeString, "\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace a\\b{function " + type[0] + " foo(){}} namespace { a\\b\\foo();}",
                    scopeStruct(returnTypeString, "\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                },
                //using an alias
                {
                    "namespace a{function " + type[0] + " foo(){}} namespace a\\a\\c{ use a as b; b\\foo();}",
                    scopeStruct(returnTypeString, "\\a\\", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 1, 0)
                },
                {
                    "namespace a{function " + type[0] + " foo(){}} namespace a\\a\\c{ use a as b; b\\foo();}",
                    scopeStruct(returnTypeString, "\\a\\", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 1, 0)
                },
                //fallback to global
                {
                    "namespace{function " + type[0] + " foo(){}} namespace a{ foo();}",
                    scopeStruct(returnTypeString, "", "\\.\\.", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0),},
                {
                    "namespace{function " + type[0] + " foo(){}} namespace a\\b{ foo();}",
                    scopeStruct(returnTypeString, "", "\\.\\.", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace{function " + type[0] + " foo(){}} namespace a\\a\\c{ foo();}",
                    scopeStruct(returnTypeString, "", "\\.\\.", 1, 1, 0, 0, 0),
                    typeStruct("fCall", returnType, 1, 1, 0, 0)
                }
            }));
        }
        return collection;
    }

    private static ReferenceScopeTestStruct[] scopeStructDefault(String type, String prefix, Integer... accessToScope) {
        return scopeStruct(type, prefix, "\\.\\.", accessToScope);
    }

    private static ReferenceScopeTestStruct[] scopeStruct(String type, String prefix, String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct[]{
            new ReferenceScopeTestStruct(prefix + "foo()", scope, Arrays.asList(accessToScope), type, "\\.")
        };
    }
}
