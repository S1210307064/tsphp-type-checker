/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.branches.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TestTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ATypeCheckTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Do;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EOF;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EXPRESSION;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EXPRESSION_LIST;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Echo;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.For;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Foreach;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.If;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Plus;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Return;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Switch;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Throw;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.VariableId;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.While;
import static org.antlr.runtime.tree.TreeParser.UP;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExpressionRootErrorTest extends ATypeCheckTest
{
    @Test
    public void ExpressionWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(EXPRESSION);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void ExpressionWithErrorAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(EXPRESSION);
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void ReturnWithErrorAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Return);
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void ReturnWithErroneousExpressionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Return);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void ThrowWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Throw);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void ThrowWithErroneousExpressionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Throw);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void IfWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(If);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void IfWithErroneousExpressionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Throw);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }


    @Test
    public void WhileWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(While);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void WhileWithErroneousExpressionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(While);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void DoWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Do);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void DoWithErroneousExpressionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
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
    public void ForWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(For);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void ForWithoutConditionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
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
    public void ForWithEmptyConditionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
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
    public void ForWithErroneousConditionAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(For);
        ast.addChild(createAst(VariableId));
        ITSPHPAst exprList = createAst(EXPRESSION_LIST);
        ast.addChild(exprList);
        exprList.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void SwitchWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Switch);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void ForeachhWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Foreach);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void TryWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void EchoWithoutChildrenBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Echo);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.expressionRoot();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }
}
