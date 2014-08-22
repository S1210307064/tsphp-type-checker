/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.coverage.reference;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.test.integration.testutils.reference.TestTSPHPReferenceWalker;
import ch.tsphp.typechecker.test.unit.testutils.AReferenceWalkerTest;
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
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class NotCorrectStartNodeTypeTest extends AReferenceWalkerTest
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

        TestTSPHPReferenceWalker walker = spy(createWalker(ast));
        Method method = TestTSPHPReferenceWalker.class.getMethod(methodName);
        method.invoke(walker);

        try {
            verify(walker).reportError(any(NoViableAltException.class));
        } catch (Exception e) {
            fail(methodName + " failed - verify caused exception:\n" + e.getClass().getName() + e.getMessage());
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"accessModifier", Try},
                {"actualParameters", Try},
                {"array", Try},
                {"arrayKeyValue", Try},
                {"arrayOrResourceOrObject", Try},
                {"assignOperator", Try},
                {"atom", Try},
                {"binaryOperatorExcludingAssign", Try},
                {"breakContinue", Try},
                {"caseLabels", Try},
                {"classBody", Try},
                {"classBodyDefinition", Try},
                {"classInterfaceType", Try},
                {"classMemberDefinition", Try},
                {"classStaticAccess", Try},
                {"compilationUnit", Try},
                {"constDefinitionList", Try},
                {"constructDefinition", Try},
                {"definition", Try},
                {"exit", Try},
                {"expression", Try},
                {"expressionList", Try},
                {"foreachLoop", Try},
                {"forLoop", Try},
                {"functionCall", Try},
                {"functionDefinition", Try},
                {"interfaceBody", Try},
                {"interfaceBodyDefinition", Try},
                {"interfaceDefinition", Try},
                {"methodCall", Try},
                {"methodCallee", Try},
                {"methodCallStatic", Try},
                {"methodDefinition", Try},
                {"methodModifier", Try},
                {"namespace", Try},
                {"namespaceBody", Try},
                {"operator", Try},
                {"parameterDeclaration", Try},
                {"parameterDeclarationList", Try},
                {"postFixExpression", Try},
                {"primitiveAtomWithConstant", Try},
                {"returnTypeModifier", Try},
                {"statement", Try},
                {"staticAccessor", Try},
                {"thisVariable", Try},
                {"unaryOperator", Try},
                {"unaryPrimitiveAtom", Try},
                {"useDeclaration", Try},
                {"useDefinitionList", Try},
                {"variable", Try},
                {"variableModifier", Try},
                {"voidType", Try},
                {"whileLoop", Try},
        });
    }
}

