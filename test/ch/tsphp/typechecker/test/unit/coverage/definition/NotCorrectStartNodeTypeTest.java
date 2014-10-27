/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.coverage.definition;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.definition.TestTSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.unit.testutils.ADefinitionWalkerTest;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Try;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class NotCorrectStartNodeTypeTest extends ADefinitionWalkerTest
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

        TestTSPHPDefinitionWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPDefinitionWalker.class.getMethod(methodName);
        method.invoke(walker);

        try {
            verify(walker).reportError(any(NoViableAltException.class));
        } catch (Exception e) {
            fail(methodName + " failed - verify caused exception:\n" + e.getClass().getName() + e.getMessage());
        }
    }

    @Test
    public void withBacktracking_StateFailedIsTrue()
            throws RecognitionException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ITSPHPAst ast = createAst(tokenType);

        TestTSPHPDefinitionWalker walker = createWalker(ast);
        walker.setBacktrackingLevel(1);
        Method method = TestTSPHPDefinitionWalker.class.getMethod(methodName);
        method.invoke(walker);

        assertThat(methodName + " failed - state was false. ", walker.getState().failed, is(true));
        assertThat(methodName + " failed - next node was different. ", treeNodeStream.LA(1), is(tokenType));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"allTypesWithoutMixedAndResource", Try},
                {"blockConditional", Try},
                {"bottomup", Try},
                {"classDefinition", Try},
                //requires parameters
//                {"constantDeclaration", Try},
                {"constantDefinitionList", Try},
                {"constructDefinition", Try},
                {"exitNamespace", Try},
                {"exitScope", Try},
                {"foreachLoop", Try},
                {"interfaceDefinition", Try},
                {"methodFunctionCall", Try},
                {"methodFunctionDefinition", Try},
                {"namespaceDefinition", Try},
                {"parameterDeclaration", Try},
                {"parameterDeclarationList", Try},
                {"returnBreakContinue", Try},
                {"topdown", Try},
                {"useDeclaration", Try},
                {"useDefinitionList", Try},
                //requires parameters
//                {"variableDeclaration", Try},
                {"variableDeclarationList", Try},
        });
    }
}

