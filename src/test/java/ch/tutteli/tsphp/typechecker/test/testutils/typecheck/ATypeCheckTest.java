/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.testutils.typecheck;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceTest;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public class ATypeCheckTest extends AReferenceTest
{

    public static EBuiltInType Bool = EBuiltInType.Bool;
    public static EBuiltInType Int = EBuiltInType.Int;
    public static EBuiltInType Float = EBuiltInType.Float;
    public static EBuiltInType String = EBuiltInType.String;
    public static EBuiltInType Array = EBuiltInType.Array;
    //
    protected TSPHPTypeCheckWalker typeCheckWalker;
    protected TypeCheckStruct[] testStructs;

    public ATypeCheckTest(String testString, TypeCheckStruct[] structs) {
        super(testString);
        testStructs = structs;
    }

    protected void verifyTypeCheck() {
        for (int i = 0; i < testStructs.length; ++i) {
            TypeCheckStruct testStruct = testStructs[i];
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast, testString, testStruct.accessOrderToNode);
            Assert.assertNotNull(testString + " failed. testCandidate is null. should be " + testStruct.evalType, testCandidate);
            Assert.assertEquals(testString + " failed. wrong ast text,", testStruct.astText,
                    testCandidate.getText());
            Assert.assertEquals(testString + " failed. wrong type,", getTypeSymbol(testStruct.evalType),
                    testCandidate.getEvalType());
        }
    }

    private ITypeSymbol getTypeSymbol(EBuiltInType type) {
        ITypeSymbol typeSymbol;

        switch (type) {
            case Bool:
                typeSymbol = symbolTable.getBoolTypeSymbol();
                break;
            case Int:
                typeSymbol = symbolTable.getIntTypeSymbol();
                break;
            case Float:
                typeSymbol = symbolTable.getFloatTypeSymbol();
                break;
            case String:
                typeSymbol = symbolTable.getStringTypeSymbol();
                break;
            case Array:
            default:
                typeSymbol = symbolTable.getArrayTypeSymbol();
                break;
        }
        return typeSymbol;
    }

    @Override
    protected void verifyReferences() {
        commonTreeNodeStream.reset();
        typeCheckWalker = new TSPHPTypeCheckWalker(commonTreeNodeStream, controller);
        typeCheckWalker.downup(ast);
        checkErrors();
    }

    private void checkErrors() {
        IErrorReporter errorHelper = ErrorReporterRegistry.get();
        junit.framework.Assert.assertFalse(testString + " failed. Exceptions occured." + errorHelper.getExceptions(),
                errorHelper.hasFoundError());

        verifyTypeCheck();
    }
}
