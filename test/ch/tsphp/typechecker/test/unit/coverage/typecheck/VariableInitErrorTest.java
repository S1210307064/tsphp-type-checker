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
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EOF;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.EXPRESSION;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Plus;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.TYPE_NAME;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.VARIABLE_DECLARATION_LIST;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.VariableId;
import static org.antlr.runtime.tree.TreeParser.UP;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class VariableInitErrorTest extends ATypeCheckWalkerTest
{

    @Test
    public void NotCorrectStartNodeAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst parent = createAst(EXPRESSION);
        ITSPHPAst ast = createAst(Try);
        parent.addChild(ast);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.variableInit();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void EmptyVariableDeclarationListAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst parent = createAst(EXPRESSION);
        ITSPHPAst ast = createAst(VARIABLE_DECLARATION_LIST);
        parent.addChild(ast);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.variableInit();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void ErroneousVariableDeclaration_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst parent = createAst(EXPRESSION);
        ITSPHPAst ast = createAst(VARIABLE_DECLARATION_LIST);
        parent.addChild(ast);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(createVariable());
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = spy(createWalker(ast));
        walker.variableInit();

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void ErroneousVariableDeclarationAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst parent = createAst(EXPRESSION);
        ITSPHPAst ast = createAst(VARIABLE_DECLARATION_LIST);
        parent.addChild(ast);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(createVariable());
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.variableInit();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(VariableId));
    }

    @Test
    public void ErroneousVariableInitialisationAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst parent = createAst(EXPRESSION);
        ITSPHPAst ast = createAst(VARIABLE_DECLARATION_LIST);
        parent.addChild(ast);
        ast.addChild(createAst(TYPE_NAME));
        ITSPHPAst variable = createVariable();
        variable.addChild(createAst(Plus));
        ast.addChild(variable);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.variableInit();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void ErroneousVariableList_reportEarlyExitException() throws RecognitionException {
        ITSPHPAst parent = createAst(EXPRESSION);
        ITSPHPAst ast = createAst(VARIABLE_DECLARATION_LIST);
        parent.addChild(ast);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = spy(createWalker(ast));
        walker.variableInit();

        verify(walker).reportError(any(EarlyExitException.class));
    }

    @Test
    public void ErroneousVariableListAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst parent = createAst(EXPRESSION);
        ITSPHPAst ast = createAst(VARIABLE_DECLARATION_LIST);
        parent.addChild(ast);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.variableInit();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }
}

