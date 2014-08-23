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

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.CLASS_STATIC_ACCESS;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.FUNCTION_CALL;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL_STATIC;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.TYPE_NAME;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.VariableId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class SymbolErrorTest extends ATypeCheckWalkerTest
{

    @Test
    public void FunctionCallWithWrongIdentifierAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(FUNCTION_CALL);
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void MethodCallWithWrongIdentifierAndBacktrackingEnabled_stateFailedIsTrue() throws RecognitionException {
        ITSPHPAst ast = createAst(METHOD_CALL);
        ast.addChild(createAst(VariableId));
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void StaticMethodCallWithWrongCalleeIdentifierAndBacktrackingEnabled_stateFailedIsTrue()
            throws RecognitionException {
        ITSPHPAst ast = createAst(METHOD_CALL_STATIC);
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void StaticMethodCallWithWrongIdentifierAndBacktrackingEnabled_stateFailedIsTrue()
            throws RecognitionException {
        ITSPHPAst ast = createAst(METHOD_CALL_STATIC);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void StaticClassAccessWithWrongIdentifier_reportNoViableAltException()
            throws RecognitionException {
        ITSPHPAst ast = createAst(CLASS_STATIC_ACCESS);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = spy(createWalker(ast));
        walker.symbol();

        ArgumentCaptor<NoViableAltException> captor = ArgumentCaptor.forClass(NoViableAltException.class);
        verify(walker).reportError(captor.capture());
        assertThat(captor.getValue().token.getType(), is(Try));
    }

    @Test
    public void StaticClassAccessWithWrongIdentifierAndBacktrackingEnabled_stateFailedIsTrue()
            throws RecognitionException {
        ITSPHPAst ast = createAst(CLASS_STATIC_ACCESS);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(createAst(Try));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }
}
