package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceAstTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class ResolveStaticClassMemberTest extends AReferenceAstTest
{

    public ResolveStaticClassMemberTest(String testString, ScopeTestStruct[] testStructs) {
        super(testString, testStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {
                        "class a{static public float $d;} a::$d;",
                        new ScopeTestStruct[]{
                                accessor("a", "\\.\\.", 1, 1, 0, 0),
                                functionDefault(1, 1, 0, 1)
                        }
                },
                {
                        "namespace{class a{static public float $d;} a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("a", "\\.\\.", 1, 1, 0, 0),
                                functionDefault(1, 1, 0, 1)
                        }
                },
                {
                        "namespace a{class a{public static float $d;} a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("a", "\\a\\.\\a\\.", 1, 1, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 0, 1)
                        }
                },
                {
                        "namespace a{class a{static public float $d;}} namespace a{ a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("a", "\\a\\.\\a\\.", 1, 1, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 0, 0, 1)
                        }
                },
                {
                        "namespace{ a::$d;} namespace{class a{static public float $d;}}",
                        new ScopeTestStruct[]{
                                accessor("a", "\\.\\.", 0, 1, 0, 0, 0),
                                functionDefault(0, 1, 0, 0, 1)
                        }
                },
                {
                        "namespace a{class a{static public float $d;}} namespace a{ a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("a", "\\a\\.\\a\\.", 1, 1, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 0, 0, 1)
                        }
                },
                {
                        "namespace a{ a::$d;} namespace a{class a{static public float $d;}}",
                        new ScopeTestStruct[]{
                                accessor("a", "\\a\\.\\a\\.", 0, 1, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 0, 1, 0, 0, 1)
                        }
                },
                //absolute path
                {
                        "namespace{class a{static public float $d;}} namespace a{ \\a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("\\a", "\\.\\.", 1, 1, 0, 0, 0),
                                functionDefault(1, 1, 0, 0, 1)
                        }
                },
                {
                        "namespace a\\b{class a{static public float $d;}} namespace x{ \\a\\b\\a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                                variable("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 1)
                        }
                },
                //relative
                {
                        "namespace a\\b{class a{static public float $d;}} namespace a{ b\\a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                                variable("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 1)
                        }
                },
                {
                        "namespace a\\b{class a{static public float $d;}} namespace { a\\b\\a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("\\a\\b\\a", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                                variable("\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 1)
                        }
                },
                //using an alias
                {
                        "namespace a{class a{static public float $d;}} "
                                + "namespace a\\a\\c{ use a as b; b\\a::$d;}",
                        new ScopeTestStruct[]{
                                accessor("\\a\\a", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 1, 0, 1)
                        }
                },
                {
                        "namespace a{class a{static public float $d;}} "
                                + "namespace a\\a\\c{ use a\\a as b; b::$d;}",
                        new ScopeTestStruct[]{
                                accessor("b", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 1, 0, 1)
                        }
                },
                //self
                {
                        "class a{static public float $d; public function void foo(){self::$d;}}",
                        new ScopeTestStruct[]{
                                accessor("self", "\\.\\.", 1, 0, 4, 1, 4, 0, 0, 0),
                                functionDefault(1, 0, 4, 1, 4, 0, 0, 1)
                        }
                },
                {
                        "namespace a{class a{static public float $d; public function void foo(){self::$d;}}}",
                        new ScopeTestStruct[]{
                                accessor("self", "\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 0, 4, 1, 4, 0, 0, 1)
                        }
                },
                //parent
                {
                        "class a{static public float $d;}class b extends a{ public function void foo(){parent::$d;}}",
                        new ScopeTestStruct[]{
                                accessor("parent", "\\.\\.", 1, 1, 4, 0, 4, 0, 0, 0),
                                functionDefault(1, 1, 4, 0, 4, 0, 0, 1)
                        }
                },
                {
                        "class b extends a{ public function void foo(){parent::$d;}} class a{static public float $d;}",
                        new ScopeTestStruct[]{
                                accessor("parent", "\\.\\.", 1, 0, 4, 0, 4, 0, 0, 0),
                                functionDefault(1, 0, 4, 0, 4, 0, 0, 1)
                        }
                },
                {
                        "namespace a{class a{static public float $d;} "
                                + "class b extends a{ public function void foo(){parent::$d;}}}",
                        new ScopeTestStruct[]{
                                accessor("parent", "\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 4, 0, 4, 0, 0, 1)
                        }
                },
                {
                        "namespace a{class b extends a{ public function void foo(){parent::$d;}} "
                                + "class a{static public float $d;}}",
                        new ScopeTestStruct[]{
                                accessor("parent", "\\a\\.\\a\\.", 1, 0, 4, 0, 4, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 0, 4, 0, 4, 0, 0, 1)
                        }
                },
                {
                        "namespace a{class a{static public float $d;}} namespace a{ "
                                + "class b extends a{ public function void foo(){parent::$d;}}}",
                        new ScopeTestStruct[]{
                                accessor("parent", "\\a\\.\\a\\.", 1, 1, 0, 4, 0, 4, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 0, 4, 0, 4, 0, 0, 1)
                        }
                },
                {
                        "namespace a{class a{static public float $d;}} namespace x{ "
                                + "class b extends \\a\\a{ public function void foo(){parent::$d;}}}",
                        new ScopeTestStruct[]{
                                accessor("parent", "\\a\\.\\a\\.", 1, 1, 0, 4, 0, 4, 0, 0, 0),
                                variable("\\a\\.\\a\\.", 1, 1, 0, 4, 0, 4, 0, 0, 1)
                        }
                },});
    }

    public static ScopeTestStruct accessor(String callee, String scope, Integer... accessToScope) {
        return new ScopeTestStruct(callee, scope, Arrays.asList(accessToScope));
    }

    public static ScopeTestStruct functionDefault(Integer... accessToScope) {
        return variable("\\.\\.", accessToScope);
    }

    public static ScopeTestStruct variable(String scope, Integer... accessToScope) {
        return new ScopeTestStruct("$d", scope + "a.", Arrays.asList(accessToScope));
    }
}
