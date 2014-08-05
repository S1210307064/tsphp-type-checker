/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.branches.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TestTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ATypeCheckTest;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.At;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Plus;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static org.antlr.runtime.tree.TreeParser.UP;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class ExpressionErrorTest extends ATypeCheckTest
{

    @Test
    public void NotAnExpression_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.expression();

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void NotAnExpressionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void AtWithErroneousExpressionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(At);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }
}

