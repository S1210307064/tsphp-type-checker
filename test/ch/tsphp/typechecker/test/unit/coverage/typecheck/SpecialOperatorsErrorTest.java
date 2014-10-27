/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.coverage.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TestTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ATypeCheckWalkerTest;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Clone;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Instanceof;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.New;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Plus;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.QuestionMark;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static org.antlr.runtime.tree.TreeParser.UP;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class SpecialOperatorsErrorTest extends ATypeCheckWalkerTest
{
    @Test
    public void TernaryWithErroneousConditionAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(QuestionMark);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.specialOperators();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void TernaryWithErroneousTrueCaseAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(QuestionMark);
        ast.addChild(createVariable());
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.specialOperators();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void TernaryWithErroneousFalseCaseAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(QuestionMark);
        ast.addChild(createVariable());
        ast.addChild(createVariable());
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.specialOperators();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void InstanceOfWithErroneousExpressionAndBacktrackingEnabled_StateFailedIsTrue() throws
            RecognitionException {
        ITSPHPAst ast = createAst(Instanceof);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.specialOperators();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void InstanceOfWithWrongIdentifier_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Instanceof);
        ast.addChild(createVariable());
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = spy(createWalker(ast));
        walker.specialOperators();

        ArgumentCaptor<NoViableAltException> captor = ArgumentCaptor.forClass(NoViableAltException.class);
        verify(walker).reportError(captor.capture());
        assertThat(captor.getValue().token.getType(), is(Try));
    }

    @Test
    public void InstanceOfWithWrongIdentifierAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(Instanceof);
        ast.addChild(createVariable());
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.specialOperators();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void NewWithWrongIdentifierAndBacktrackingEnabled_StateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(New);
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.specialOperators();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void CloneWithErroneousExpressionAndBacktrackingEnabled_StateFailedIsTrue() throws
            RecognitionException {
        ITSPHPAst ast = createAst(Clone);
        ast.addChild(createAst(Plus));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.specialOperators();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }
}
