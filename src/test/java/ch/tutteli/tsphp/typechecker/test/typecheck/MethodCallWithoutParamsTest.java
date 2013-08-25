/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Object;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.Resource;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.String;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.AOperatorTypeCheckTest.StringNullable;
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
public class MethodCallWithoutParamsTest extends AReferenceScopeTypeCheckTest
{

    public MethodCallWithoutParamsTest(String testString, ReferenceScopeTestStruct[] scopeTestStructs,
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
        String dfault = "\\.\\.";

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

        String kind = "class";
        String body = "{}";
        for (Object[] type : types) {
            String returnTypeString = (String) type[0];
            if (returnTypeString.charAt(0) == '\\') {
                returnTypeString = returnTypeString.substring(1);
            }
            EBuiltInType returnType = (EBuiltInType) type[1];

            for (int i = 0; i < 2; ++i) {
                collection.addAll(Arrays.asList(new Object[][]{
                    {
                        kind + " a{function " + type[0] + " foo()" + body + "} a $a; $a->foo();",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 1, 2, 0, 0),
                            functionDefault(returnTypeString, 1, 2, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 2, 0)
                    },
                    {
                        "namespace{" + kind + " a{function " + type[0] + " foo()" + body + "} a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 1, 2, 0, 0),
                            functionDefault(returnTypeString, 1, 2, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 2, 0)
                    },
                    {
                        "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "} a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 1, 2, 0, 0),
                            function(returnTypeString, "\\a\\.\\a\\.", 1, 2, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 2, 0)
                    },
                    {
                        "namespace {" + kind + " a{function " + type[0] + " foo()" + body + "}} namespace { a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 1, 1, 1, 0, 0),
                            functionDefault(returnTypeString, 1, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 1, 0)
                    },
                    {
                        "namespace{ a $a; $a->foo();} namespace{" + kind + " a{function " + type[0] + " foo()" + body + "}}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", dfault, 0, 1, 1, 0, 0),
                            functionDefault(returnTypeString, 0, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 0, 1, 1, 0)
                    },
                    {
                        "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}} namespace a{ a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                            function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 1, 0)
                    },
                    {
                        "namespace a{ a $a; $a->foo();} namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 0, 1, 1, 0, 0),
                            function(returnTypeString, "\\a\\.\\a\\.", 0, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 0, 1, 1, 0)
                    },
                    //absolute path
                    {
                        "namespace{" + kind + " a{function " + type[0] + " foo()" + body + "}} namespace a{ \\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", dfault, "$a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                            functionDefault(returnTypeString, 1, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 1, 0)
                    },
                    {
                        "namespace a\\b{" + kind + " a{function " + type[0] + " foo()" + body + "}} namespace x{ \\a\\b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\b\\.\\a\\b\\.", "$a", "\\x\\.\\x\\.", 1, 1, 1, 0, 0),
                            function(returnTypeString, "\\a\\b\\.\\a\\b\\.", 1, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 1, 0)
                    },
                    //relative
                    {
                        "namespace a\\b{" + kind + " a{function " + type[0] + " foo()" + body + "}} namespace a{ b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\b\\.\\a\\b\\.", "$a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                            function(returnTypeString, "\\a\\b\\.\\a\\b\\.", 1, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 1, 0)
                    },
                    {
                        "namespace a\\b{" + kind + " a{function " + type[0] + " foo()" + body + "}} namespace { a\\b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\b\\.\\a\\b\\.", "$a", dfault, 1, 1, 1, 0, 0),
                            function(returnTypeString, "\\a\\b\\.\\a\\b\\.", 1, 1, 1, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 1, 0)
                    },
                    //using an alias
                    {
                        "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                        + "namespace a\\a\\c{ use a as b; b\\a $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\a\\c\\.\\a\\a\\c\\.", 1, 1, 2, 0, 0),
                            function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 2, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 2, 0)
                    },
                    {
                        "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                        + "namespace a\\a\\c{ use a\\a as b; b $a; $a->foo();}",
                        new ReferenceScopeTestStruct[]{
                            callee("a", "\\a\\.\\a\\.", "$a", "\\a\\a\\c\\.\\a\\a\\c\\.", 1, 1, 2, 0, 0),
                            function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 2, 0, 1)
                        },
                        typeStruct("mCall", returnType, 1, 1, 2, 0)
                    }
                }));
                kind = "interface";
                body = ";";
            }
            collection.addAll(Arrays.asList(new Object[][]{
                //this
                {
                    "class a{function " + type[0] + " foo(){}} "
                    + "class b extends a{function void bar(){$this->foo();}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("b", dfault, "$this", dfault, 1, 1, 4, 0, 4, 0, 0, 0),
                        functionDefault(returnTypeString, 1, 1, 4, 0, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "namespace a{class a{function " + type[0] + " foo(){}} "
                    + "class b extends a{function void bar(){$this->foo();}}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("b", "\\a\\.\\a\\.", "$this", "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 0),
                        function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "class a{function " + type[0] + " foo(){} function void bar(){$this->foo();}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("a", dfault, "$this", dfault, 1, 0, 4, 1, 4, 0, 0, 0),
                        functionDefault(returnTypeString, 1, 0, 4, 1, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                },
                {
                    "namespace a{class a{function " + type[0] + " foo(){} function void bar(){$this->foo();}}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("a", "\\a\\.\\a\\.", "$this", "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 0),
                        function(returnTypeString, "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                },
                //self
                {
                    "class a{function " + type[0] + " foo(){} function void bar(){self::foo();}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("a", dfault, "self", dfault, 1, 0, 4, 1, 4, 0, 0, 0),
                        functionDefault(returnTypeString, 1, 0, 4, 1, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                },
                {
                    "namespace a{class a{function " + type[0] + " foo(){} function void bar(){self::foo();}}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("a", "\\a\\.\\a\\.", "self", "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 0),
                        function(returnTypeString, "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                },
                {
                    "class a{function " + type[0] + " foo(){}} "
                    + "class b extends a{function void bar(){self::foo();}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("b", dfault, "self", dfault, 1, 1, 4, 0, 4, 0, 0, 0),
                        functionDefault(returnTypeString, 1, 1, 4, 0, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "namespace a{class a{function " + type[0] + " foo(){} } "
                    + "class b extends a{function void bar(){self::foo();}}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("b", "\\a\\.\\a\\.", "self", "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 0),
                        function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                },
                //parent
                {
                    "class a{function " + type[0] + " foo(){}} "
                    + "class b extends a{function void bar(){parent::foo();}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("a", dfault, "parent", dfault, 1, 1, 4, 0, 4, 0, 0, 0),
                        functionDefault(returnTypeString, 1, 1, 4, 0, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                },
                {
                    "namespace a{class a{function " + type[0] + " foo(){} } "
                    + "class b extends a{function void bar(){parent::foo();}}} ",
                    new ReferenceScopeTestStruct[]{
                        callee("a", "\\a\\.\\a\\.", "parent", "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 0),
                        function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 1)
                    },
                    typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                }
            }));
        }

        return collection;
    }

    private static ReferenceScopeTestStruct callee(String type, String typeScope,
            String callee, String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct(callee, scope, Arrays.asList(accessToScope), type, typeScope);
    }

    private static ReferenceScopeTestStruct functionDefault(String type, Integer... accessToScope) {
        return function(type, "\\.\\.", accessToScope);
    }

    private static ReferenceScopeTestStruct function(String type, String scope, Integer... accessToScope) {
        return new ReferenceScopeTestStruct("foo()", scope + "a.", Arrays.asList(accessToScope), type, "\\.");
    }
}
