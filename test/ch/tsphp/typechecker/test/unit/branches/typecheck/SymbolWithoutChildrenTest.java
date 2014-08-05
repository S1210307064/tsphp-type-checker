/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.branches.typecheck;

import ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ANodeWithoutChildrenTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.CLASS_STATIC_ACCESS;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.FUNCTION_CALL;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL_STATIC;

@RunWith(Parameterized.class)
public class SymbolWithoutChildrenTest extends ANodeWithoutChildrenTest
{

    public SymbolWithoutChildrenTest(String theTestCase, int theTokenType) {
        super(theTestCase, theTokenType);
    }

    @Override
    public void walk(TSPHPTypeCheckWalker walker) throws RecognitionException {
        walker.symbol();
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"function call", FUNCTION_CALL},
                {"method call", METHOD_CALL},
                {"static method call", METHOD_CALL_STATIC},
                {"static class access", CLASS_STATIC_ACCESS},
        });
    }
}

