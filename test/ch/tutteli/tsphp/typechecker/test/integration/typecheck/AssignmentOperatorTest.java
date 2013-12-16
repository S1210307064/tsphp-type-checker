package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AOperatorTypeCheckTest;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.AssignHelper;
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
public class AssignmentOperatorTest extends AOperatorTypeCheckTest
{

    private static List<Object[]> collection;

    public AssignmentOperatorTest(String testString, TypeCheckStruct[] struct) {
        super(testString, struct);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        AssignHelper.addAssignments(collection, false);

        addCompoundAssignment();

        collection.addAll(Arrays.asList(new Object[][]{
                //see TSPHP - 433
                {"bool? $b; int $i = $b = true;", new TypeCheckStruct[]{
                        struct("$i", Int, 1, 1, 1),
                        struct("=", Bool, 1, 1, 1, 0),
                        struct("$b", BoolNullable, 1, 1, 1, 0, 0),
                        struct("true", Bool, 1, 1, 1, 0, 1)
                }
                },
                {"float $b; string $c; int $i = $b = $c = false;", new TypeCheckStruct[]{
                        struct("$i", Int, 1, 2, 1),
                        struct("=", Bool, 1, 2, 1, 0),
                        struct("$b", Float, 1, 2, 1, 0, 0),
                        struct("=", Bool, 1, 2, 1, 0, 1),
                        struct("$c", String, 1, 2, 1, 0, 1, 0),
                        struct("false", Bool, 1, 2, 1, 0, 1, 1)
                }
                },
        }));

        return collection;
    }

    private static void addCompoundAssignment() {

        String[] allAssignOperators = new String[]{"+=", "-=", "/=", "*=", "&=", "|=", "^=", "%=", "<<=", ">>="};
        for (String operator : allAssignOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"int $a=0; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                    {"int $a=0; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                    {"int $a=0; bool? $b=false; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Int, 1,
                            2, 0)}},
                    {"int $a=0; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                    {"int $a=0; int? $b=false; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Int, 1, 2,
                            0)}},
                    {"int? $a=null; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                    {"int? $a=null; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                    {
                            "int? $a=null; bool? $b=false; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct("=", Int, 1, 2, 0)}
                    },
                    {"int? $a=null; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Int, 1, 1, 0)}},
                    {"int? $a=null; int? $b=false; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Int,
                            1, 2, 0)}},}));
        }
        String[] floatCompatibleAssignOperators = new String[]{"+=", "-=", "/=", "*=", "%="};
        for (String operator : floatCompatibleAssignOperators) {
            collection.addAll(Arrays.asList(new Object[][]{
                    {"float $a=0.0; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {"float $a=0.0; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {"float $a=0.0; bool? $b=false; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=",
                            Float, 1, 2, 0)}},
                    {"float $a=0.0; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {"float $a=0.0; int? $b=null; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=", Float,
                            1, 2, 0)}},
                    {"float $a=0.0; $a " + operator + " 1.0;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {"float $a=0.0; float? $b=null; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=",
                            Float, 1, 2, 0)}},
                    {"float? $a=null; $a " + operator + " true;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {"float? $a=null; $a " + operator + " false;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {
                            "float? $a=null; bool? $b=false; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}
                    },
                    {"float? $a=null; $a " + operator + " 1;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {
                            "float? $a=null; int? $b=null; $a " + operator + " $b;",
                            new TypeCheckStruct[]{struct("=", Float, 1, 2, 0)}
                    },
                    {"float? $a=null; $a " + operator + " 1.0;", new TypeCheckStruct[]{struct("=", Float, 1, 1, 0)}},
                    {"float? $a=null; float? $b=null; $a " + operator + " $b;", new TypeCheckStruct[]{struct("=",
                            Float, 1, 2, 0)}}
            }));
        }
        collection.addAll(Arrays.asList(new Object[][]{
                {"string $a=''; $a .= true;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string $a=''; $a .= false;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string $a=''; $a .= 1;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string $a=''; $a .= 1.0;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string $a=''; $a .= 'hello';", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string $a=''; string? $b=null; $a .= $b;", new TypeCheckStruct[]{struct("=", String, 1, 2, 0)}},
                {"string? $a=null; $a .= false;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string? $a=null; $a .= false;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string? $a=null; $a .= 1;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string? $a=null; $a .= 1.0;", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string? $a=null; $a .= 'hello';", new TypeCheckStruct[]{struct("=", String, 1, 1, 0)}},
                {"string? $a=null; string? $b=''; $a .= $b;", new TypeCheckStruct[]{struct("=", String, 1, 2, 0)}}
        }));

    }
}
