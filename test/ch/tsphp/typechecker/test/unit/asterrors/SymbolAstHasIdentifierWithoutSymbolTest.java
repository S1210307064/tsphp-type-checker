/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.asterrors;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TestTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ATypeCheckWalkerTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.CLASS_STATIC_ACCESS;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.CONSTANT;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.FUNCTION_CALL;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Identifier;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.METHOD_CALL_STATIC;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.PARAMETER_LIST;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.TYPE_NAME;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

//tests due to TSPHP-734 increment constant causes NullPointerException
public class SymbolAstHasIdentifierWithoutSymbolTest extends ATypeCheckWalkerTest
{

    @Test
    public void FunctionCall_IdentifierWithoutSymbol_CreatesErroneousTypeSymbol() throws
            RecognitionException {
        ITSPHPAst ast = createAst(FUNCTION_CALL);
        ITSPHPAst identifier = createAst(TYPE_NAME);
        ast.addChild(identifier);
        ast.addChild(createAst(PARAMETER_LIST));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(false));
        verify(typeCheckPhaseController).createErroneousTypeForMissingSymbol(identifier);
    }

    @Test
    public void MethodCall_IdentifierWithoutSymbol_CreatesErroneousTypeSymbol() throws
            RecognitionException {
        ITSPHPAst ast = createAst(METHOD_CALL);
        ITSPHPAst identifier = createAst(Identifier);
        ast.addChild(createVariable());
        ast.addChild(identifier);
        ast.addChild(createAst(PARAMETER_LIST));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(false));
        verify(typeCheckPhaseController).createErroneousTypeForMissingSymbol(identifier);
    }

    @Test
    public void MethodCallStatic_IdentifierWithoutSymbol_CreatesErroneousTypeSymbol() throws
            RecognitionException {
        ITSPHPAst ast = createAst(METHOD_CALL_STATIC);
        ITSPHPAst identifier = createAst(Identifier);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(identifier);
        ast.addChild(createAst(PARAMETER_LIST));

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(false));
        verify(typeCheckPhaseController).createErroneousTypeForMissingSymbol(identifier);
    }

    @Test
    public void ClassStaticAccess_IdentifierWithoutSymbol_CreatesErroneousTypeSymbol() throws
            RecognitionException {
        ITSPHPAst ast = createAst(CLASS_STATIC_ACCESS);
        ITSPHPAst identifier = createAst(CONSTANT);
        ast.addChild(createAst(TYPE_NAME));
        ast.addChild(identifier);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        walker.symbol();

        assertThat(walker.getState().failed, is(false));
        verify(typeCheckPhaseController).createErroneousTypeForMissingSymbol(identifier);
    }
}
