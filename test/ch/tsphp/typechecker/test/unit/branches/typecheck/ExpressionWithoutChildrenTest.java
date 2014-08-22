/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.branches.typecheck;

import ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ANodeWithoutChildrenTypeCheckWalkerTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Assign;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.At;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.CAST;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Equal;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Identical;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Instanceof;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL_POSTFIX;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Plus;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.UNARY_PLUS;

@RunWith(Parameterized.class)
public class ExpressionWithoutChildrenTest extends ANodeWithoutChildrenTypeCheckWalkerTest
{

    public ExpressionWithoutChildrenTest(String theTestCase, int theTokenType) {
        super(theTestCase, theTokenType);
    }

    @Override
    public void walk(TSPHPTypeCheckWalker walker) throws RecognitionException {
        walker.expression();
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"Method call", METHOD_CALL},
                {"Unary", UNARY_PLUS},
                {"Binary", Plus},
                {"At", At},
                {"Equality", Equal},
                {"Identity", Identical},
                {"Assign", Assign},
                {"CAST", CAST},
                {"specialOperators", Instanceof},
                {"postfixOperators", METHOD_CALL_POSTFIX},
        });
    }
}

