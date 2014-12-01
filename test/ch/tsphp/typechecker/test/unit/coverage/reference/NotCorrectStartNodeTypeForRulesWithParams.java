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

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Try;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class NotCorrectStartNodeTypeForRulesWithParams extends AReferenceWalkerTest
{
    @Test
    public void allTypes_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.allTypes(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void array_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.arrayOrResourceOrMixed(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void classInterfaceType_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.classInterfaceType(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void classExtendsDeclaration_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.classExtendsDeclaration(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }


    @Test
    public void constDeclaration_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.constDeclaration(mock(ITypeSymbol.class));

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void implementsDeclaration_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.implementsDeclaration(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void interfaceExtendsDeclaration_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.interfaceExtendsDeclaration(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void returnTypes_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.returnTypes(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }

    @Test
    public void scalarTypes_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.scalarTypes(new TSPHPAst());

        verify(walker).reportError(any(NoViableAltException.class));
    }


    @Test
    public void variableDeclaration_WrongStartNode_reportNoViableAltException() throws RecognitionException {
        ITSPHPAst ast = createAst(Try);

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        walker.variableDeclaration(mock(ITypeSymbol.class), false);

        verify(walker).reportError(any(NoViableAltException.class));
    }

}

