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

import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.testutils.definition.ADefinitionScopeTest;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.ReferenceScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AReferenceAstTypeCheckTest;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Array;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.ErrorException;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Exception;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.FloatNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.IntNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Object;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Void;
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
public class MethodCallStaticTest extends AReferenceAstTypeCheckTest
{

    public MethodCallStaticTest(String testString, ScopeTestStruct[] scopeTestStructs,
            TypeCheckStruct[] typeCheckStructs) {
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
            EBuiltInType returnType = (EBuiltInType) type[1];
            collection.addAll(Arrays.asList(new Object[][]{
                {
                    "class a{static function " + type[0] + " foo(){}} a::foo();",
                    new ScopeTestStruct[]{
                        callee("a", "\\.\\.", 1, 1, 0, 0),
                        functionDefault(1, 1, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0)
                },
                {
                    "namespace{class a{static function " + type[0] + " foo(){}} a::foo();}",
                    new ScopeTestStruct[]{
                        callee("a", "\\.\\.", 1, 1, 0, 0),
                        functionDefault(1, 1, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0)
                },
                {
                    "namespace a{class a{static function " + type[0] + " foo(){}} a::foo();}",
                    new ScopeTestStruct[]{
                        callee("a", "\\a\\.\\a\\.", 1, 1, 0, 0),
                        function("\\a\\.\\a\\.", 1, 1, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0)
                },
                {
                    "namespace a{class a{static function " + type[0] + " foo(){}}} namespace a{ a::foo();}",
                    new ScopeTestStruct[]{
                        callee("a", "\\a\\.\\a\\.", 1, 1, 0, 0, 0),
                        function("\\a\\.\\a\\.", 1, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace{ a::foo();} namespace{class a{static function " + type[0] + " foo(){}}}",
                    new ScopeTestStruct[]{
                        callee("a", "\\.\\.", 0, 1, 0, 0, 0),
                        functionDefault(0, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 0, 1, 0, 0)
                },
                {
                    "namespace a{class a{static function " + type[0] + " foo(){}}} namespace a{ a::foo();}",
                    new ScopeTestStruct[]{
                        callee("a", "\\a\\.\\a\\.", 1, 1, 0, 0, 0),
                        function("\\a\\.\\a\\.", 1, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace a{ a::foo();} namespace a{class a{static function " + type[0] + " foo(){}}}",
                    new ScopeTestStruct[]{
                        callee("a", "\\a\\.\\a\\.", 0, 1, 0, 0, 0),
                        function("\\a\\.\\a\\.", 0, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 0, 1, 0, 0)
                },
                //absolute path
                {
                    "namespace{class a{static function " + type[0] + " foo(){}}} namespace a{ \\a::foo();}",
                    new ScopeTestStruct[]{
                        callee("\\a", "\\.\\.", 1, 1, 0, 0, 0),
                        functionDefault(1, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace a\\b{class a{static function " + type[0] + " foo(){}}} namespace x{ \\a\\b\\a::foo();}",
                    new ScopeTestStruct[]{
                        callee("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                        function("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0, 0)
                },
                //relative
                {
                    "namespace a\\b{class a{static function " + type[0] + " foo(){}}} namespace a{ b\\a::foo();}",
                    new ScopeTestStruct[]{
                        callee("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                        function("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0, 0)
                },
                {
                    "namespace a\\b{class a{static function " + type[0] + " foo(){}}} namespace { a\\b\\a::foo();}",
                    new ScopeTestStruct[]{
                        callee("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                        function("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 0, 0)
                },
                //using an alias
                {
                    "namespace a{class a{static function " + type[0] + " foo(){}}} "
                    + "namespace a\\a\\c{ use a as b; b\\a::foo();}",
                    new ScopeTestStruct[]{
                        callee("\\a\\a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                        function("\\a\\.\\a\\.", 1, 1, 1, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 1, 0)
                },
                {
                    "namespace a{class a{static function " + type[0] + " foo(){}}} "
                    + "namespace a\\a\\c{ use a\\a as b; b::foo();}",
                    new ScopeTestStruct[]{
                        callee("b", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                        function("\\a\\.\\a\\.", 1, 1, 1, 0, 1)
                    },
                    typeStruct("smCall", returnType, 1, 1, 1, 0)
                }
            }));
        }
        return collection;
    }

    private static ScopeTestStruct callee(String callee, String scope, Integer... accessToScope) {
        return new ScopeTestStruct(callee, scope, Arrays.asList(accessToScope));
    }

    private static ScopeTestStruct functionDefault(Integer... accessToScope) {
        return function("\\.\\.", accessToScope);
    }

    private static ScopeTestStruct function(String scope, Integer... accessToScope) {
        return new ScopeTestStruct("foo()", scope + "a.", Arrays.asList(accessToScope));
    }
}
