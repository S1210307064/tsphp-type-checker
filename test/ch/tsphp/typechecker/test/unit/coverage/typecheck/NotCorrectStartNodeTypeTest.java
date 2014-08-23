/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.coverage.typecheck;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TestTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.test.unit.testutils.ATypeCheckWalkerTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.Try;
import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.VariableId;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class NotCorrectStartNodeTypeTest extends ATypeCheckWalkerTest
{
    private String methodName;
    private int tokenType;

    public NotCorrectStartNodeTypeTest(String theMethodName, int theTokenType) {
        methodName = theMethodName;
        tokenType = theTokenType;
    }

    @Test
    public void withoutBacktracking_reportNoViableAltException()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(tokenType);

        TestTSPHPTypeCheckWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPTypeCheckWalker.class.getMethod(methodName);
        method.invoke(walker);

        try {
            ArgumentCaptor<RecognitionException> captor = ArgumentCaptor.forClass(RecognitionException.class);
            verify(walker).reportError(captor.capture());
            assertThat(methodName + " - failed. Wrong token type", captor.getValue().token.getType(), is(tokenType));
        } catch (Exception e) {
            fail(methodName + " failed - verify caused exception:\n" + e.getClass().getName() + e.getMessage());
        }
    }

    @Test
    public void withBacktracking_stateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(tokenType);

        TestTSPHPTypeCheckWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        Method method = TestTSPHPTypeCheckWalker.class.getMethod(methodName);
        method.invoke(walker);

        assertThat(methodName + " failed - state was false. ", walker.getState().failed, is(true));
        assertThat(methodName + " failed - next node was different. ", treeNodeStream.LA(1), is(tokenType));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"allTypes", Try},
                {"assignOperator", Try},
                {"binaryOperator", Try},
                {"bottomup", VariableId},
                {"castOperator", Try},
                {"classInterfaceDefinition", Try},
                {"constantInit", Try},
                {"echo", Try},
                {"equalityOperator", Try},
                {"expression", Try},
                {"expressionLists", Try},
                {"expressionRoot", VariableId},
                {"foreachLoop", Try},
                {"identityOperator", Try},
                {"parameterDefaultValue", Try},
                {"postFixOperators", Try},
                {"specialOperators", Try},
                {"symbol", Try},
                {"tryCatch", VariableId},
                {"unaryOperator", Try},
                {"variableInit", Try},
        });
    }
}

