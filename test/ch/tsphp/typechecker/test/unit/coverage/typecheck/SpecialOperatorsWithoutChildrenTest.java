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

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Clone;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Instanceof;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.New;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.QuestionMark;

@RunWith(Parameterized.class)
public class SpecialOperatorsWithoutChildrenTest extends ANodeWithoutChildrenTypeCheckWalkerTest
{

    public SpecialOperatorsWithoutChildrenTest(String theTestCase, int theTokenType) {
        super(theTestCase, theTokenType);
    }

    @Override
    public void walk(TSPHPTypeCheckWalker walker) throws RecognitionException {
        walker.specialOperators();
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"QuestionMark", QuestionMark},
                {"Instanceof", Instanceof},
                {"New", New},
                {"Clone", Clone},
        });
    }
}

