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

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.test.testutils.ScopeTestHelper;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.Array;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.Bool;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.BoolNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.Float;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.FloatNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.Int;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.IntNullable;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.Object;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.Resource;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.String;
import static ch.tutteli.tsphp.typechecker.test.testutils.typecheck.EBuiltInType.StringNullable;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AOperatorTypeCheckTest extends ATypeCheckTest
{

    public static EBuiltInType Bool = EBuiltInType.Bool;
    public static EBuiltInType BoolNullable = EBuiltInType.BoolNullable;
    public static EBuiltInType Int = EBuiltInType.Int;
    public static EBuiltInType IntNullable = EBuiltInType.IntNullable;
    public static EBuiltInType Float = EBuiltInType.Float;
    public static EBuiltInType FloatNullable = EBuiltInType.FloatNullable;
    public static EBuiltInType String = EBuiltInType.String;
    public static EBuiltInType StringNullable = EBuiltInType.StringNullable;
    public static EBuiltInType Array = EBuiltInType.Array;
    public static EBuiltInType Resource = EBuiltInType.Resource;
    public static EBuiltInType Object = EBuiltInType.Object;
    public static EBuiltInType Exception = EBuiltInType.Exception;
    public static EBuiltInType ErrorException = EBuiltInType.ErrorException;
    public static EBuiltInType Null = EBuiltInType.Null;
    //
    protected TypeCheckStruct[] testStructs;

    public AOperatorTypeCheckTest(String testString, TypeCheckStruct[] structs) {
        super(testString);
        testStructs = structs;
    }

    @Override
    protected void verifyTypeCheck() {
        for (int i = 0; i < testStructs.length; ++i) {
            TypeCheckStruct testStruct = testStructs[i];
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast, testString, testStruct.accessOrderToNode);
            Assert.assertNotNull(testString + " -> " + testStruct.astText + " failed. testCandidate is null. should be " + testStruct.evalType, testCandidate);
            Assert.assertEquals(testString + " -> " + testStruct.astText + " failed. wrong ast text,", testStruct.astText,
                    testCandidate.getText());
            Assert.assertEquals(testString + " -> " + testStruct.astText + " failed. wrong type,", getTypeSymbol(testStruct.evalType),
                    testCandidate.getEvalType());
        }
    }

    protected static TypeCheckStruct struct(String astText, EBuiltInType type, Integer... accessOrder) {
        return new TypeCheckStruct(astText, type, Arrays.asList(accessOrder));
    }

    private ITypeSymbol getTypeSymbol(EBuiltInType type) {
        ITypeSymbol typeSymbol;

        switch (type) {
            case Bool:
                typeSymbol = symbolTable.getBoolTypeSymbol();
                break;
            case BoolNullable:
                typeSymbol = symbolTable.getBoolNullableTypeSymbol();
                break;
            case Int:
                typeSymbol = symbolTable.getIntTypeSymbol();
                break;
            case IntNullable:
                typeSymbol = symbolTable.getIntNullableTypeSymbol();
                break;
            case Float:
                typeSymbol = symbolTable.getFloatTypeSymbol();
                break;
            case FloatNullable:
                typeSymbol = symbolTable.getFloatNullableTypeSymbol();
                break;
            case String:
                typeSymbol = symbolTable.getStringTypeSymbol();
                break;
            case StringNullable:
                typeSymbol = symbolTable.getStringNullableTypeSymbol();
                break;
            case Array:
                typeSymbol = symbolTable.getArrayTypeSymbol();
                break;
            case Resource:
                typeSymbol = symbolTable.getResourceTypeSymbol();
                break;
            case Exception:
                typeSymbol = symbolTable.getExceptionTypeSymbol();
                break;
            case ErrorException:
                typeSymbol = (ITypeSymbol) definer.getGlobalDefaultNamespace().resolve(
                        astHelper.createAst(TSPHPDefinitionWalker.TYPE_NAME, "ErrorException"));
                break;
            case Null:
                typeSymbol = symbolTable.getNullTypeSymbol();
                break;
            case Object:
            default:
                typeSymbol = symbolTable.getObjectTypeSymbol();
                break;
        }
        return typeSymbol;
    }
}
