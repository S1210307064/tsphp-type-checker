package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class SwitchErrorTest extends ATypeCheckErrorTest
{

    public SwitchErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("switch", 2, 1)};

        //wrong switch condition type
        String[] types = new String[]{"array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;\n switch($b){}", errorDto});
        }

        // case label not same type or sub-type of condition
        errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 2, 1)};
        types = new String[]{"bool?", "int", "int?", "float", "float?", "string", "string?",
                "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"bool $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"int", "int?", "float", "float?", "string", "string?",
                "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"bool? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"bool?", "int?", "float", "float?", "string", "string?", "array", "resource",
                "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"int $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"float", "float?", "string", "string?", "array", "resource", "object",
                "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"int? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"bool?", "int?", "float?", "string", "string?", "array", "resource", "object",
                "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"float $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"string", "string?", "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"float? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"bool?", "int?", "string?", "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"string $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"string? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        return collection;
    }
}
