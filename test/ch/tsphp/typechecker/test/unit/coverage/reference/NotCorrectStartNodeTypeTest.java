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

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Else;
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
                {"accessModifier", Else},
                {"actualParameters", Else},
                {"array", Else},
                {"arrayKeyValue", Else},
                {"assignOperator", Else},
                {"atom", Else},
                {"binaryOperatorExcludingAssign", Else},
                {"block", Else},
                {"blockConditional", Else},
                {"breakContinue", Else},
                {"caseLabels", Else},
                {"classBody", Else},
                {"classBodyDefinition", Else},
                {"fieldDefinition", Else},
                {"catchBlocks", Else},
                {"classStaticAccess", Else},
                {"compilationUnit", Else},
                {"constDefinitionList", Else},
                {"constructDefinition", Else},
                {"definition", Else},
                {"doWhileLoop", Else},
                {"exit", Else},
                {"expression", Else},
                {"expressionList", Else},
                {"foreachLoop", Else},
                {"forLoop", Else},
                {"functionCall", Else},
                {"functionDefinition", Else},
                {"ifCondition", Else},
                {"instruction", Else},
                {"instructions", Else},
                {"interfaceBody", Else},
                {"interfaceBodyDefinition", Else},
                {"interfaceDefinition", Else},
                {"methodCall", Else},
                {"methodCallee", Else},
                {"methodCallStatic", Else},
                {"methodDefinition", Else},
                {"methodModifier", Else},
                {"namespace", Else},
                {"namespaceBody", Else},
                {"operator", Else},
                {"parameterDeclaration", Else},
                {"parameterDeclarationList", Else},
                {"postFixExpression", Else},
                {"primitiveAtomWithConstant", Else},
                {"statement", Else},
                {"staticAccessor", Else},
                {"switchCondition", Else},
                {"switchContents", Else},
                {"thisVariable", Else},
                {"tryCatch", Else},
                {"unaryOperator", Else},
                {"unaryPrimitiveAtom", Else},
                {"useDeclaration", Else},
                {"useDefinitionList", Else},
                {"variable", Else},
                {"voidType", Else},
                {"whileLoop", Else},
        });
    }
}

