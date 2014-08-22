/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.coverage.typecheck;

import ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ANodeWithoutChildrenTypeCheckWalkerTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Do;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EXPRESSION;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Echo;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.For;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Foreach;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.If;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Switch;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Throw;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.While;

@RunWith(Parameterized.class)
public class ExpressionRootWithoutChildrenTest extends ANodeWithoutChildrenTypeCheckWalkerTest
{

    public ExpressionRootWithoutChildrenTest(String theTestCase, int theTokenType) {
        super(theTestCase, theTokenType);
    }

    @Override
    public void walk(TSPHPTypeCheckWalker walker) throws RecognitionException {
        walker.expressionRoot();
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"expression", EXPRESSION},
                {"throw", Throw},
                {"if", If},
                {"while", While},
                {"do", Do},
                {"for", For},
                {"switch", Switch},
                {"foreach", Foreach},
                {"try", Try},
                {"echo", Echo},
        });
    }
}

