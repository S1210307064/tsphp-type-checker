package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class BreakContinueErrorTest extends AReferenceErrorTest
{

    public BreakContinueErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        ReferenceErrorDto[] errorBreakDto = new ReferenceErrorDto[]{new ReferenceErrorDto("break", 2, 1)};
        ReferenceErrorDto[] errorContinueDto = new ReferenceErrorDto[]{new ReferenceErrorDto("continue", 2, 1)};


        String[][] loops = new String[][]{
                //switch is treated like a loop in PHP
                {"int $a=1; switch($a){case 1:", "}"},
                {"for(;;){", "}"},
                {"foreach([1,2] as object $v){", "}"},
                {"while(true){", "}"},
                {"do{", "}while(true);"}
        };
        String[][] loops2 = new String[][]{
                //switch is treated like a loop in PHP
                {"int $b=3; switch($b){case 1:", "}"},
                {"for(;;){", "}"},
                {"foreach([1,2] as object $v2){", "}"},
                {"while(true){", "}"},
                {"do{", "}while(true);"}
        };
        String[][] loops3 = new String[][]{
                //switch is treated like a loop in PHP
                {"int $c=3; switch($c){case 1:", "}"},
                {"for(;;){", "}"},
                {"foreach([1,2] as object $v3){", "}"},
                {"while(true){", "}"},
                {"do{", "}while(true);"}
        };

        collection.add(new Object[]{"if(true){\n break;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n break 1;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n break 2;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n break 3;}", errorBreakDto});
        collection.add(new Object[]{"if(true){\n continue;}", errorContinueDto});
        collection.add(new Object[]{"if(true){\n continue 1;}", errorContinueDto});
        collection.add(new Object[]{"if(true){\n continue 2;}", errorContinueDto});
        collection.add(new Object[]{"if(true){\n continue 3;}", errorContinueDto});

        for (String[] loop : loops) {

            collection.add(new Object[]{loop[0] + "\n break 2;" + loop[1], errorBreakDto});
            collection.add(new Object[]{loop[0] + "\n continue 2;" + loop[1], errorContinueDto});
            for (String[] loop2 : loops2) {
                collection.add(new Object[]{loop[0] + loop2[0] + "\n break 4;" + loop2[1] + loop[1], errorBreakDto});
                collection.add(new Object[]{
                        loop[0] + loop2[0] + "\n continue 4;" + loop2[1] + loop[1],
                        errorContinueDto
                });
                for (String[] loop3 : loops3) {
                    collection.add(new Object[]{
                            loop[0] + loop2[0] + loop3[0] + "\n break 30;" + loop3[1] + loop2[1] + loop[1],
                            errorBreakDto
                    });
                    collection.add(new Object[]{
                            loop[0] + loop2[0] + loop3[0] + "\n continue 30;" + loop3[1] + loop2[1] + loop[1],
                            errorContinueDto
                    });
                }
            }
        }

        return collection;
    }
}
