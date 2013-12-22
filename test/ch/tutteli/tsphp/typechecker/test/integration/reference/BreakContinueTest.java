package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AVerifyTimesReferenceTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class BreakContinueTest extends AVerifyTimesReferenceTest
{

    public BreakContinueTest(String testString) {
        super(testString, 1);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Override
    protected void verifyTimes() {
        verify(controller).checkBreakContinueLevel(any(ITSPHPAst.class), any(ITSPHPAst.class));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

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
                {"int $b=1; switch($b){case 1:", "}"},
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

        for (String[] loop : loops) {
            collection.add(new Object[]{loop[0] + "break;" + loop[1]});
            collection.add(new Object[]{loop[0] + "break 1;" + loop[1]});
            collection.add(new Object[]{loop[0] + "continue;" + loop[1]});
            collection.add(new Object[]{loop[0] + "continue 1;" + loop[1]});
            for (String[] loop2 : loops2) {
                collection.add(new Object[]{loop[0] + loop2[0] + "break 2;" + loop2[1] + loop[1]});
                collection.add(new Object[]{loop[0] + loop2[0] + "continue 2;" + loop2[1] + loop[1]});
                for (String[] loop3 : loops3) {
                    collection.add(new Object[]{
                            loop[0] + loop2[0] + loop3[0] + "break 3;" + loop3[1] + loop2[1] + loop[1]
                    });
                    collection.add(new Object[]{
                            loop[0] + loop2[0] + loop3[0] + "continue 3;" + loop3[1] + loop2[1] + loop[1]
                    });
                }
            }

        }

        return collection;
    }
}
