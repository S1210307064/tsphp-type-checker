/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import org.junit.Assert;
import org.junit.Ignore;

import java.util.Arrays;

@Ignore
public abstract class AOperatorTypeCheckTest extends ATypeCheckTest
{

    public static final EBuiltInType Bool = EBuiltInType.Bool;
    public static final EBuiltInType BoolFalseable = EBuiltInType.BoolFalseable;
    public static final EBuiltInType BoolNullable = EBuiltInType.BoolNullable;
    public static final EBuiltInType BoolFalseableAndNullable = EBuiltInType.BoolFalseableAndNullable;
    public static final EBuiltInType Int = EBuiltInType.Int;
    public static final EBuiltInType IntFalseable = EBuiltInType.IntFalseable;
    public static final EBuiltInType IntNullable = EBuiltInType.IntNullable;
    public static final EBuiltInType IntFalseableAndNullable = EBuiltInType.IntFalseableAndNullable;
    public static final EBuiltInType Float = EBuiltInType.Float;
    public static final EBuiltInType FloatFalseable = EBuiltInType.FloatFalseable;
    public static final EBuiltInType FloatNullable = EBuiltInType.FloatNullable;
    public static final EBuiltInType FloatFalseableAndNullable = EBuiltInType.FloatFalseableAndNullable;
    public static final EBuiltInType String = EBuiltInType.String;
    public static final EBuiltInType StringFalseable = EBuiltInType.StringFalseable;
    public static final EBuiltInType StringNullable = EBuiltInType.StringNullable;
    public static final EBuiltInType StringFalseableAndNullable = EBuiltInType.StringFalseableAndNullable;
    public static final EBuiltInType Array = EBuiltInType.Array;
    public static final EBuiltInType ArrayFalseable = EBuiltInType.ArrayFalseable;
    public static final EBuiltInType Resource = EBuiltInType.Resource;
    public static final EBuiltInType ResourceFalseable = EBuiltInType.ResourceFalseable;
    public static final EBuiltInType Mixed = EBuiltInType.Mixed;
    public static final EBuiltInType Exception = EBuiltInType.Exception;
    public static final EBuiltInType ExceptionFalseable = EBuiltInType.ExceptionFalseable;
    public static final EBuiltInType ErrorException = EBuiltInType.ErrorException;
    public static final EBuiltInType ErrorExceptionFalseable = EBuiltInType.ErrorExceptionFalseable;
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
            ITSPHPAst testCandidate = ScopeTestHelper.getAst(ast, testString, testStruct);

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

        IAstHelper astHelper = AstHelperRegistry.get();
        ITSPHPAst tMod = astHelper.createAst(TSPHPDefinitionWalker.TYPE_MODIFIER, "tMod");

        switch (type) {
            case Bool:
                typeSymbol = typeSystem.getBoolTypeSymbol();
                break;
            case BoolFalseable:
                typeSymbol = typeSystem.getBoolFalseableTypeSymbol();
                break;
            case BoolNullable:
                typeSymbol = typeSystem.getBoolNullableTypeSymbol();
                break;
            case BoolFalseableAndNullable:
                typeSymbol = typeSystem.getBoolFalseableAndNullableTypeSymbol();
                break;
            case Int:
                typeSymbol = typeSystem.getIntTypeSymbol();
                break;
            case IntFalseable:
                typeSymbol = typeSystem.getIntFalseableTypeSymbol();
                break;
            case IntNullable:
                typeSymbol = typeSystem.getIntNullableTypeSymbol();
                break;
            case IntFalseableAndNullable:
                typeSymbol = typeSystem.getIntFalseableAndNullableTypeSymbol();
                break;
            case Float:
                typeSymbol = typeSystem.getFloatTypeSymbol();
                break;
            case FloatFalseable:
                typeSymbol = typeSystem.getFloatFalseableTypeSymbol();
                break;
            case FloatNullable:
                typeSymbol = typeSystem.getFloatNullableTypeSymbol();
                break;
            case FloatFalseableAndNullable:
                typeSymbol = typeSystem.getFloatFalseableAndNullableTypeSymbol();
                break;
            case String:
                typeSymbol = typeSystem.getStringTypeSymbol();
                break;
            case StringFalseable:
                typeSymbol = typeSystem.getStringFalseableTypeSymbol();
                break;
            case StringNullable:
                typeSymbol = typeSystem.getStringNullableTypeSymbol();
                break;
            case StringFalseableAndNullable:
                typeSymbol = typeSystem.getStringFalseableAndNullableTypeSymbol();
                break;
            case Array:
                typeSymbol = typeSystem.getArrayTypeSymbol();
                break;
            case ArrayFalseable:
                typeSymbol = typeSystem.getArrayFalseableTypeSymbol();
                break;
            case Resource:
                typeSymbol = typeSystem.getResourceTypeSymbol();
                break;
            case ResourceFalseable:
                typeSymbol = typeSystem.getResourceFalseableTypeSymbol();
                break;
            case Exception:
                typeSymbol = typeSystem.getExceptionTypeSymbol();
                break;
            case ExceptionFalseable:
                tMod.addChild(astHelper.createAst(TSPHPDefinitionWalker.LogicNot, "!"));
                typeSymbol = referencePhaseController.resolveType(
                        astHelper.createAst(TSPHPDefinitionWalker.TYPE_NAME, "\\Exception!"), tMod);
                break;
            case ErrorException:
                typeSymbol = referencePhaseController.resolveType(
                        astHelper.createAst(TSPHPDefinitionWalker.TYPE_NAME, "\\ErrorException"), tMod);
                break;
            case ErrorExceptionFalseable:
                tMod.addChild(astHelper.createAst(TSPHPDefinitionWalker.LogicNot, "!"));
                typeSymbol = referencePhaseController.resolveType(
                        astHelper.createAst(TSPHPDefinitionWalker.TYPE_NAME, "\\ErrorException!"), tMod);
                break;
            case Null:
                typeSymbol = typeSystem.getNullTypeSymbol();
                break;
            case Void:
                typeSymbol = typeSystem.getVoidTypeSymbol();
                break;
            case Mixed:
            default:
                typeSymbol = typeSystem.getMixedTypeSymbol();
                break;
        }
        return typeSymbol;
    }
}
