package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.ReferenceScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AReferenceScopeTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

        Object[][] types = TypeHelper.getTypesInclTokenAndDefaultValue();

        int count = 0;
        for (Object[] type : types) {
            ++count;
            String kind = "class";
            String body = "{" + type[0] + " $a=" + type[2] + "; return $a;}";
            if (count == 14) {
                body = "{}";
            }

            String returnTypeString = (String) type[0];
            if (returnTypeString.charAt(0) == '\\') {
                returnTypeString = returnTypeString.substring(1);
            }
            EBuiltInType returnType = (EBuiltInType) type[1];

            for (int i = 0; i < 2; ++i) {
                collection.addAll(Arrays.asList(new Object[][]{
                        {
                                kind + " a{function " + type[0] + " foo()" + body + "} a $a=null; $a->foo();",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", dfault, "$a", dfault, 1, 2, 0, 0),
                                        functionDefault(returnTypeString, 1, 2, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 2, 0)
                        },
                        {
                                "namespace{" + kind + " a{function " + type[0] + " foo()" + body + "} "
                                        + "a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", dfault, "$a", dfault, 1, 2, 0, 0),
                                        functionDefault(returnTypeString, 1, 2, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 2, 0)
                        },
                        {
                                "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "} "
                                        + "a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 1, 2, 0, 0),
                                        function(returnTypeString, "\\a\\.\\a\\.", 1, 2, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 2, 0)
                        },
                        {
                                "namespace {" + kind + " a{function " + type[0] + " foo()" + body + "}} " +
                                        "namespace { a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", dfault, "$a", dfault, 1, 1, 1, 0, 0),
                                        functionDefault(returnTypeString, 1, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 1, 1, 0)
                        },
                        {
                                "namespace{ a $a=null; $a->foo();} "
                                        + "namespace{" + kind + " a{function " + type[0] + " foo()" + body + "}}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", dfault, "$a", dfault, 0, 1, 1, 0, 0),
                                        functionDefault(returnTypeString, 0, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 0, 1, 1, 0)
                        },
                        {
                                "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                                        + "namespace a{ a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                                        function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 1, 1, 0)
                        },
                        {
                                "namespace a{ a $a=null; $a->foo();} "
                                        + "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", "\\a\\.\\a\\.", "$a", "\\a\\.\\a\\.", 0, 1, 1, 0, 0),
                                        function(returnTypeString, "\\a\\.\\a\\.", 0, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 0, 1, 1, 0)
                        },
                        //absolute path
                        {
                                "namespace{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                                        + "namespace a{ \\a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", dfault, "$a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                                        functionDefault(returnTypeString, 1, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 1, 1, 0)
                        },
                        {
                                "namespace a\\b{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                                        + "namespace x{ \\a\\b\\a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", "\\a\\b\\.\\a\\b\\.", "$a", "\\x\\.\\x\\.", 1, 1, 1, 0, 0),
                                        function(returnTypeString, "\\a\\b\\.\\a\\b\\.", 1, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 1, 1, 0)
                        },
                        //relative
                        {
                                "namespace a\\b{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                                        + " namespace a{ b\\a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", "\\a\\b\\.\\a\\b\\.", "$a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                                        function(returnTypeString, "\\a\\b\\.\\a\\b\\.", 1, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 1, 1, 0)
                        },
                        {
                                "namespace a\\b{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                                        + "namespace { a\\b\\a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", "\\a\\b\\.\\a\\b\\.", "$a", dfault, 1, 1, 1, 0, 0),
                                        function(returnTypeString, "\\a\\b\\.\\a\\b\\.", 1, 1, 1, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 1, 1, 0)
                        },
                        //using an alias
                        {
                                "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                                        + "namespace a\\a\\c{ use a as b; b\\a $a=null; $a->foo();}",
                                new ReferenceScopeTestStruct[]{
                                        callee("a", "\\a\\.\\a\\.", "$a", "\\a\\a\\c\\.\\a\\a\\c\\.", 1, 1, 2, 0, 0),
                                        function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 2, 0, 1)
                                },
                                typeStruct("mCall", returnType, 1, 1, 2, 0)
                        },
                        {
                                "namespace a{" + kind + " a{function " + type[0] + " foo()" + body + "}} "
                                        + "namespace a\\a\\c{ use a\\a as b; b $a=null; $a->foo();}",
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

            body = "{" + type[0] + " $a=" + type[2] + "; return $a;}";
            if (count == 14) {
                body = "{}";
            }
            collection.addAll(Arrays.asList(new Object[][]{
                    //this
                    {
                            "class a{function " + type[0] + " foo()" + body + "} "
                                    + "class b extends a{function void bar(){$this->foo();}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("b", dfault, "$this", dfault, 1, 1, 4, 0, 4, 0, 0, 0),
                                    functionDefault(returnTypeString, 1, 1, 4, 0, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "namespace a{class a{function " + type[0] + " foo()" + body + "} "
                                    + "class b extends a{function void bar(){$this->foo();}}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("b", "\\a\\.\\a\\.", "$this", "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 0),
                                    function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "class a{function " + type[0] + " foo()" + body + " function void bar(){$this->foo();}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("a", dfault, "$this", dfault, 1, 0, 4, 1, 4, 0, 0, 0),
                                    functionDefault(returnTypeString, 1, 0, 4, 1, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                    },
                    {
                            "namespace a{class a{function " + type[0] + " foo()" + body + " function void bar()" +
                                    "{$this->foo();}}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("a", "\\a\\.\\a\\.", "$this", "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 0),
                                    function(returnTypeString, "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                    },
                    //self
                    {
                            "class a{function " + type[0] + " foo()" + body + " function void bar(){self::foo();}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("a", dfault, "self", dfault, 1, 0, 4, 1, 4, 0, 0, 0),
                                    functionDefault(returnTypeString, 1, 0, 4, 1, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                    },
                    {
                            "namespace a{class a{function " + type[0] + " foo()" + body
                                    + " function void bar(){self::foo();}}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("a", "\\a\\.\\a\\.", "self", "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 0),
                                    function(returnTypeString, "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 0, 4, 1, 4, 0, 0)
                    },
                    {
                            "class a{function " + type[0] + " foo()" + body + "} "
                                    + "class b extends a{function void bar(){self::foo();}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("b", dfault, "self", dfault, 1, 1, 4, 0, 4, 0, 0, 0),
                                    functionDefault(returnTypeString, 1, 1, 4, 0, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "namespace a{class a{function " + type[0] + " foo()" + body + " } "
                                    + "class b extends a{function void bar(){self::foo();}}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("b", "\\a\\.\\a\\.", "self", "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 0),
                                    function(returnTypeString, "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                    },
                    //parent
                    {
                            "class a{function " + type[0] + " foo()" + body + "} "
                                    + "class b extends a{function void bar(){parent::foo();}} ",
                            new ReferenceScopeTestStruct[]{
                                    callee("a", dfault, "parent", dfault, 1, 1, 4, 0, 4, 0, 0, 0),
                                    functionDefault(returnTypeString, 1, 1, 4, 0, 4, 0, 0, 1)
                            },
                            typeStruct("mCall", returnType, 1, 1, 4, 0, 4, 0, 0)
                    },
                    {
                            "namespace a{class a{function " + type[0] + " foo()" + body + " } "
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
