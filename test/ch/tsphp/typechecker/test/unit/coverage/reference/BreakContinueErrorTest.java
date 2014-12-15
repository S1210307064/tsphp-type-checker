/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This file is part of the TinsPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.unit.coverage.reference;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.reference.TestTSPHPReferenceWalker;
import ch.tsphp.typechecker.test.unit.testutils.AReferenceWalkerTest;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Break;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Continue;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.EOF;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.NAMESPACE_BODY;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Try;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class BreakContinueErrorTest extends AReferenceWalkerTest
{
    @Test
    public void BreakWrongSucceeding_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst parent = createAst(NAMESPACE_BODY);
        ITSPHPAst ast = createAst(Break);
        parent.addChild(ast);
        parent.addChild(createAst(Try));

        TestTSPHPReferenceWalker walker = spy(createWalker(parent));
        walker.namespaceBody();

        verify(walker).reportError(any(NoViableAltException.class));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void ContinueWrongSucceeding_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst parent = createAst(NAMESPACE_BODY);
        ITSPHPAst ast = createAst(Continue);
        parent.addChild(ast);
        parent.addChild(createAst(Try));

        TestTSPHPReferenceWalker walker = spy(createWalker(parent));
        walker.namespaceBody();

        verify(walker).reportError(any(NoViableAltException.class));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void BreakAndNotAnInt_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Break);
        ast.addChild(createAst(Try));

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.breakContinue();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void ContinueAndNotAnInt_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Continue);
        ast.addChild(createAst(Try));

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.breakContinue();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

}

