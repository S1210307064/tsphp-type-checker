/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.testutils;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TestTSPHPTypeCheckWalker;
import org.antlr.runtime.RecognitionException;
import org.junit.Ignore;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EOF;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
public abstract class ANodeWithoutChildrenTypeCheckWalkerTest extends ATypeCheckWalkerTest
{
    protected String testCase;
    protected int tokenType;


    public ANodeWithoutChildrenTypeCheckWalkerTest(String theTestCase, int theTokenType) {
        testCase = theTestCase;
        tokenType = theTokenType;
    }

    public abstract void walk(TSPHPTypeCheckWalker walker) throws RecognitionException;

    public void check() throws RecognitionException {
        ITSPHPAst ast = createAst(tokenType);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walk(walker);

        assertThat(testCase, walker.getState().failed, is(true));
        assertThat(testCase, treeNodeStream.LA(1), is(EOF));
    }
}
