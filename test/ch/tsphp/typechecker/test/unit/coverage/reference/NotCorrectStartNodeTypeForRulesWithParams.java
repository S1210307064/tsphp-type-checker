/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.coverage.reference;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.TSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.test.integration.testutils.reference.TestTSPHPReferenceWalker;
import ch.tsphp.typechecker.test.unit.testutils.AReferenceWalkerTest;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Try;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class NotCorrectStartNodeTypeForRulesWithParams extends AReferenceWalkerTest
{
    @Test
    public void allTypes_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("allTypes", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void arrayOrResourceOrMixed_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("arrayOrResourceOrMixed", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void block_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("block", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void classInterfaceType_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("classInterfaceType", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void classExtendsDeclaration_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("classExtendsDeclaration", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }


    @Test
    public void constDeclaration_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("constDeclaration", ITypeSymbol.class);
        method.invoke(walker, mock(ITypeSymbol.class));

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void implementsDeclaration_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("implementsDeclaration", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void instructions_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("instructions", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void instruction_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("instruction", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void ifCondition_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("ifCondition", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void blockConditional_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("blockConditional", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void switchCondition_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("switchCondition", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void switchContents_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("switchContents", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void doWhileLoop_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("doWhileLoop", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void tryCatch_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("tryCatch", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void catchBlocks_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("catchBlocks", boolean.class);
        method.invoke(walker, false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void interfaceExtendsDeclaration_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("interfaceExtendsDeclaration", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }


    @Test
    public void returnTypes_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("returnTypes", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void parameterNormalOrOptional_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("parameterNormalOrOptional", ITypeSymbol.class);
        method.invoke(walker, mock(ITypeSymbol.class));

        verify(walker).reportError(any(NoViableAltException.class));
    }


    @Test
    public void scalarTypes_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod("scalarTypes", ITSPHPAst.class);
        method.invoke(walker, new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }


    @Test
    public void variableDeclaration_WrongStartNode_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod(
                "variableDeclaration", ITypeSymbol.class, boolean.class);
        method.invoke(walker, mock(ITypeSymbol.class), false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

}

