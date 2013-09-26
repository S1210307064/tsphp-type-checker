package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AReferenceAstTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MethodCallStaticWithoutParamsTest extends AReferenceAstTypeCheckTest
{

    public MethodCallStaticWithoutParamsTest(String testString, ScopeTestStruct[] scopeTestStructs,
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
