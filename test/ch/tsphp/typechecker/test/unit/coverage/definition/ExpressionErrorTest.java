/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class ExpressionErrorTest from the TinsPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.unit.coverage.definition;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.definition.TestTSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.unit.testutils.ADefinitionWalkerTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.CAST;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.EOF;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.EXPRESSION;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Instanceof;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TYPE;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Try;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeInt;
import static org.antlr.runtime.tree.TreeParser.UP;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;

public class ExpressionErrorTest extends ADefinitionWalkerTest
{
    @Test
    public void emptyCast_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CAST);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void castWithoutTypeNode_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CAST);
        //should be TYPE
        ast.addChild(createAst(Try));

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void castWithEmptyTypeNode_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CAST);
        ast.addChild(createAst(TYPE));

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void castWithErroneousType_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CAST);
        ITSPHPAst type = createAst(TYPE);
        //should be a primitive type
        type.addChild(createAst(Try));
        ast.addChild(type);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void castWithSuperfluousChildInType_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CAST);
        ITSPHPAst type = createAst(TYPE);
        type.addChild(createAst(TypeInt));
        type.addChild(createAst(Try));
        ast.addChild(type);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }


    @Test
    public void castWithSuperfluousChild_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CAST);
        ITSPHPAst type = createAst(TYPE);
        type.addChild(createAst(TypeInt));
        ast.addChild(type);
        type.addChild(createAst(EXPRESSION));
        type.addChild(createAst(Try));

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EXPRESSION));
    }

    @Test
    public void emptyInstanceOf_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Instanceof);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void instanceOfWithErroneousRHS_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Instanceof);
        ast.addChild(createAst(EXPRESSION));
        ast.addChild(createAst(Try));

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }


    @Test
    public void instanceOfWithSuperfluousChild_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Instanceof);
        ast.addChild(createAst(EXPRESSION));
        ast.addChild(createVariable());
        ast.addChild(createAst(Try));

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.expression();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(Try));
    }
}

