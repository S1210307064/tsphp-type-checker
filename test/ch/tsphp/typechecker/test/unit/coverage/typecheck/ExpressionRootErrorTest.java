/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.coverage.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TestTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ATypeCheckWalkerTest;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Do;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EXPRESSION;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EXPRESSION_LIST;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.For;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Plus;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Return;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Throw;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.VariableId;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.While;
import static org.antlr.runtime.tree.TreeParser.UP;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ExpressionRootErrorTest extends ATypeCheckWalkerTest
{
    @Test
    public void ExpressionWithErrorAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(EXPRESSION);
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void ReturnWithErrorAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Return);
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void ReturnWithErroneousExpressionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Return);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void ThrowWithErroneousExpressionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Throw);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void IfWithErroneousExpressionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Throw);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void WhileWithErroneousExpressionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(While);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void DoWithErroneousExpressionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Do);
        ast.addChild(createAst(EXPRESSION));
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void ForWithoutConditionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(For);
        ast.addChild(createAst(VariableId));
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Plus));
    }

    @Test
    public void ForWithEmptyConditionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(For);
        ast.addChild(createAst(VariableId));
        ITSPHPAst exprList = createAst(EXPRESSION_LIST);
        ast.addChild(exprList);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void ForWithErroneousConditionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(For);
        ast.addChild(createAst(VariableId));
        ITSPHPAst exprList = createAst(EXPRESSION_LIST);
        exprList.addChild(createAst(Plus));
        ast.addChild(exprList);


        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void ForWithErroneousExpressionList_reportEarlyExitException() throws RecognitionException {
        ITSPHPAst ast = createAst(For);
        ast.addChild(createAst(VariableId));
        ITSPHPAst exprList = createAst(EXPRESSION_LIST);
        exprList.addChild(createAst(Try));
        ast.addChild(exprList);

        TestTSPHPTypeCheckWalker walker = spy(createWalker(ast));
        walker.expressionRoot();

        verify(walker).reportError(any(EarlyExitException.class));
    }

    @Test
    public void ForWithErroneousExpressionListAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(For);
        ast.addChild(createAst(VariableId));
        ITSPHPAst exprList = createAst(EXPRESSION_LIST);
        exprList.addChild(createAst(Try));
        ast.addChild(exprList);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }
}
