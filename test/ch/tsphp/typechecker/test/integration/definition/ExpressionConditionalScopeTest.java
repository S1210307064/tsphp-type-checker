/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class ExpressionConditionalScopeTest from the TinsPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import ch.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionScopeTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class ExpressionConditionalScopeTest extends ADefinitionScopeTest
{

    public ExpressionConditionalScopeTest(String testString, ScopeTestStruct[] theTestStructs) {
        super(testString, theTestStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        //variable usage in conditional blocks
        Object[][] conditions = new Object[][]{
                {"if(true){", "}", new Integer[]{1, 0, 1}},
                {"switch($a){case 1:", "}", new Integer[]{1, 0, 2}},
                {"for(;;){", "}", new Integer[]{1, 0, 3}},
                {"while(true){", "}", new Integer[]{1, 0, 1}}
        };

        for (Object[] condition : conditions) {
            collection.addAll(ScopeTestHelper.testStrings((String) condition[0], (String) condition[1],
                    "\\.\\.cScope", (Integer[]) condition[2]));
            collection.addAll(ScopeTestHelper.testStrings("namespace a{" + condition[0], condition[1] + "}",
                    "\\a\\.\\a\\.cScope", (Integer[]) condition[2]));
            collection.addAll(ScopeTestHelper.testStrings("namespace a\\b{" + condition[0], condition[1] + "}",
                    "\\a\\b\\.\\a\\b\\.cScope", (Integer[]) condition[2]));
        }

        return collection;
    }
}
