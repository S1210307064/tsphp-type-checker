/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class ConstantDefinitionListErrorTest from the TinsPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.unit.coverage.definition;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.definition.TestTSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.unit.testutils.ADefinitionWalkerTest;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.InvocationTargetException;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.CONSTANT_DECLARATION;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.CONSTANT_DECLARATION_LIST;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.EOF;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Identifier;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TYPE;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TYPE_MODIFIER;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Try;
import static org.antlr.runtime.tree.TreeParser.UP;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class ConstantDefinitionListErrorTest extends ADefinitionWalkerTest
{
    @Test
    public void emptyConstantDeclarationList_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CONSTANT_DECLARATION_LIST);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.constantDefinitionList();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void missingType_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CONSTANT_DECLARATION_LIST);
        ast.addChild(createAst(CONSTANT_DECLARATION));

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.constantDefinitionList();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(CONSTANT_DECLARATION));
    }

    @Test
    public void emptyType_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CONSTANT_DECLARATION_LIST);
        ITSPHPAst type = createAst(TYPE);
        ast.addChild(type);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.constantDefinitionList();

        assertThat(walker.getState().failed, is(true));
        assertThat(treeNodeStream.LA(1), is(UP));
    }

    @Test
    public void missingTypeModifier_WithoutBacktracking_ReportEarlyExitException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CONSTANT_DECLARATION_LIST);
        ITSPHPAst type = createAst(TYPE);
        type.addChild(createAst(Identifier));
        ast.addChild(type);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.constantDefinitionList();

        ArgumentCaptor<EarlyExitException> captor = ArgumentCaptor.forClass(EarlyExitException.class);
        verify(walker).reportError(captor.capture());
        assertThat(captor.getValue().token.getType(), is(EOF));
    }

    @Test
    public void missingTypeModifier_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CONSTANT_DECLARATION_LIST);
        ITSPHPAst type = createAst(TYPE);
        type.addChild(createAst(Identifier));
        ast.addChild(type);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.constantDefinitionList();

        assertThat(walker.getState().failed, is(true));
        //EOF due to matchAny
        assertThat(treeNodeStream.LA(1), is(EOF));
    }

    @Test
    public void superfluousChildInType_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CONSTANT_DECLARATION_LIST);
        ITSPHPAst type = createAst(TYPE);
        type.addChild(createAst(TYPE_MODIFIER));
        type.addChild(createAst(Identifier));
        type.addChild(createAst(Try));
        ast.addChild(type);

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.constantDefinitionList();

        assertThat(walker.getState().failed, is(true));
        //EOF due to matchAny
        assertThat(treeNodeStream.LA(1), is(Try));
    }

    @Test
    public void erroneousVariableDeclaration_BacktrackingEnabled_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(CONSTANT_DECLARATION_LIST);
        ITSPHPAst type = createAst(TYPE);
        type.addChild(createAst(TYPE_MODIFIER));
        type.addChild(createAst(Identifier));
        ast.addChild(type);
        //should be VariableId
        ast.addChild(createAst(Try));

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        walker.setBacktrackingLevel(1);
        walker.constantDefinitionList();

        assertThat(walker.getState().failed, is(true));
        //EOF due to matchAny
        assertThat(treeNodeStream.LA(1), is(Try));
    }
}

