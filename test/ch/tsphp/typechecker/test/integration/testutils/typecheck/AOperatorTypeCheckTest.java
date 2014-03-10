/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import org.junit.Assert;
import org.junit.Ignore;

import java.util.Arrays;

@Ignore
public abstract class AOperatorTypeCheckTest extends ATypeCheckTest
{

    public static final EBuiltInType Bool = EBuiltInType.Bool;
    public static final EBuiltInType BoolNullable = EBuiltInType.BoolNullable;
    public static final EBuiltInType Int = EBuiltInType.Int;
    public static final EBuiltInType IntNullable = EBuiltInType.IntNullable;
    public static final EBuiltInType Float = EBuiltInType.Float;
    public static final EBuiltInType FloatNullable = EBuiltInType.FloatNullable;
    public static final EBuiltInType String = EBuiltInType.String;
    public static final EBuiltInType StringNullable = EBuiltInType.StringNullable;
    public static final EBuiltInType Array = EBuiltInType.Array;
    public static final EBuiltInType Resource = EBuiltInType.Resource;
    public static final EBuiltInType Object = EBuiltInType.Object;
    public static final EBuiltInType Exception = EBuiltInType.Exception;
    public static final EBuiltInType ErrorException = EBuiltInType.ErrorException;
    public static final EBuiltInType Null = EBuiltInType.Null;
    public static final EBuiltInType Void = EBuiltInType.Void;
    //
    protected TypeCheckStruct[] testStructs;

    public AOperatorTypeCheckTest(String testString, TypeCheckStruct[] structs) {
        super(testString);
        testStructs = structs;
    }

    @Override
    protected void verifyTypeCheck() {
        for (TypeCheckStruct testStruct : testStructs) {
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast, testString, testStruct.accessOrderToNode);
            Assert.assertNotNull(testString + " -> " + testStruct.astText + " failed. testCandidate is null. should " +
                    "be " + testStruct.evalType, testCandidate);
            Assert.assertEquals(testString + " -> " + testStruct.astText + " failed. wrong ast text,",
                    testStruct.astText,
                    testCandidate.getText());
            Assert.assertEquals(testString + " -> " + testStruct.astText + " failed. wrong type,",
                    getTypeSymbol(testStruct.evalType),
                    testCandidate.getEvalType());
        }
    }

    protected static TypeCheckStruct struct(String astText, EBuiltInType type, Integer... accessOrder) {
        return new TypeCheckStruct(astText, type, Arrays.asList(accessOrder));
    }

    protected static TypeCheckStruct[] typeStruct(String astText, EBuiltInType type, Integer... accessOrder) {
        return new TypeCheckStruct[]{struct(astText, type, accessOrder)};
    }

    protected ITypeSymbol getTypeSymbol(EBuiltInType type) {
        ITypeSymbol typeSymbol;

        switch (type) {
            case Bool:
                typeSymbol = typeSystem.getBoolTypeSymbol();
                break;
            case BoolNullable:
                typeSymbol = typeSystem.getBoolNullableTypeSymbol();
                break;
            case Int:
                typeSymbol = typeSystem.getIntTypeSymbol();
                break;
            case IntNullable:
                typeSymbol = typeSystem.getIntNullableTypeSymbol();
                break;
            case Float:
                typeSymbol = typeSystem.getFloatTypeSymbol();
                break;
            case FloatNullable:
                typeSymbol = typeSystem.getFloatNullableTypeSymbol();
                break;
            case String:
                typeSymbol = typeSystem.getStringTypeSymbol();
                break;
            case StringNullable:
                typeSymbol = typeSystem.getStringNullableTypeSymbol();
                break;
            case Array:
                typeSymbol = typeSystem.getArrayTypeSymbol();
                break;
            case Resource:
                typeSymbol = typeSystem.getResourceTypeSymbol();
                break;
            case Exception:
                typeSymbol = typeSystem.getExceptionTypeSymbol();
                break;
            case ErrorException:
                typeSymbol = (ITypeSymbol) definer.getGlobalDefaultNamespace().resolve(
                        AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.TYPE_NAME, "ErrorException"));
                break;
            case Null:
                typeSymbol = typeSystem.getNullTypeSymbol();
                break;
            case Void:
                typeSymbol = typeSystem.getVoidTypeSymbol();
                break;
            case Object:
            default:
                typeSymbol = typeSystem.getObjectTypeSymbol();
                break;
        }
        return typeSymbol;
    }
}
