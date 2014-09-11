/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.INullTypeSymbol;
import ch.tsphp.typechecker.symbols.IPseudoTypeSymbol;
import ch.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.IVoidTypeSymbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Assign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.At;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.BitwiseAnd;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.BitwiseAndAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.BitwiseNot;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.BitwiseOr;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.BitwiseOrAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.BitwiseXor;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.BitwiseXorAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Bool;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.CAST;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.CAST_ASSIGN;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.CLASS_MODIFIER;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Divide;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.DivideAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Dot;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.DotAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Equal;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Float;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.GreaterEqualThan;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.GreaterThan;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Identical;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Identifier;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Int;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LessEqualThan;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LessThan;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LogicAnd;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LogicAndWeak;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LogicNot;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LogicOr;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LogicOrWeak;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.LogicXorWeak;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.METHOD_MODIFIER;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Minus;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.MinusAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Modulo;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.ModuloAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Multiply;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.MultiplyAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.NotEqual;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.NotIdentical;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.POST_DECREMENT;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.POST_INCREMENT;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.PRE_DECREMENT;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.PRE_INCREMENT;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Plus;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.PlusAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Public;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.QuestionMark;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.ShiftLeft;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.ShiftLeftAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.ShiftRight;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.ShiftRightAssign;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.String;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TYPE_MODIFIER;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TYPE_NAME;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeArray;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeBool;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeFloat;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeInt;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TypeString;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.UNARY_MINUS;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.UNARY_PLUS;
import static ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker.VariableId;

public class TypeSystem implements ITypeSystem
{

    private final ISymbolFactory symbolFactory;
    private final IAstHelper astHelper;
    //
    private final Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private final Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();
    private final Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> implicitCastings = new HashMap<>();
    private final Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> explicitCastings = new HashMap<>();
    //
    private INullTypeSymbol nullTypeSymbol;
    private IScalarTypeSymbol boolTypeSymbol;
    private IScalarTypeSymbol boolFalseableTypeSymbol;
    private IScalarTypeSymbol boolNullableTypeSymbol;
    private IScalarTypeSymbol boolFalseableAndNullableTypeSymbol;
    private IScalarTypeSymbol intTypeSymbol;
    private IScalarTypeSymbol intFalseableTypeSymbol;
    private IScalarTypeSymbol intNullableTypeSymbol;
    private IScalarTypeSymbol intFalseableAndNullableTypeSymbol;
    private IScalarTypeSymbol floatTypeSymbol;
    private IScalarTypeSymbol floatFalseableTypeSymbol;
    private IScalarTypeSymbol floatNullableTypeSymbol;
    private IScalarTypeSymbol floatFalseableAndNullableTypeSymbol;
    private IScalarTypeSymbol stringTypeSymbol;
    private IScalarTypeSymbol stringFalseableTypeSymbol;
    private IScalarTypeSymbol stringNullableTypeSymbol;
    private IScalarTypeSymbol stringFalseableAndNullableTypeSymbol;
    private IArrayTypeSymbol arrayTypeSymbol;
    private IArrayTypeSymbol arrayFalseableTypeSymbol;
    private IPseudoTypeSymbol resourceTypeSymbol;
    private IPseudoTypeSymbol resourceFalseableTypeSymbol;
    private IPseudoTypeSymbol mixedTypeSymbol;
    private IClassTypeSymbol exceptionTypeSymbol;

    private final IGlobalNamespaceScope globalDefaultNamespace;
    private IVoidTypeSymbol voidTypeSymbol;

    public TypeSystem(ISymbolFactory theSymbolFactory, IAstHelper theAstHelper,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {

        symbolFactory = theSymbolFactory;
        astHelper = theAstHelper;
        globalDefaultNamespace = theGlobalDefaultNamespace;

        defineBuiltInTypes();
        initMaps();
        defineOperators();
        defineImplicitCastings();
        defineExplicitCastings();
    }

    @Override
    public Map<Integer, List<IMethodSymbol>> getUnaryOperators() {
        return unaryOperators;
    }

    @Override
    public Map<Integer, List<IMethodSymbol>> getBinaryOperators() {
        return binaryOperators;
    }

    @Override
    public Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> getImplicitCasting() {
        return implicitCastings;
    }

    @Override
    public Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> getExplicitCastings() {
        return explicitCastings;
    }

    @Override
    public IVoidTypeSymbol getVoidTypeSymbol() {
        return voidTypeSymbol;
    }

    @Override
    public INullTypeSymbol getNullTypeSymbol() {
        return nullTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getBoolTypeSymbol() {
        return boolTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getBoolFalseableTypeSymbol() {
        return boolFalseableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getBoolNullableTypeSymbol() {
        return boolNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getBoolFalseableAndNullableTypeSymbol() {
        return boolFalseableAndNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getIntTypeSymbol() {
        return intTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getIntFalseableTypeSymbol() {
        return intFalseableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getIntNullableTypeSymbol() {
        return intNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getIntFalseableAndNullableTypeSymbol() {
        return intFalseableAndNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getFloatTypeSymbol() {
        return floatTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getFloatFalseableTypeSymbol() {
        return floatFalseableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getFloatNullableTypeSymbol() {
        return floatNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getFloatFalseableAndNullableTypeSymbol() {
        return floatFalseableAndNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getStringTypeSymbol() {
        return stringTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getStringFalseableTypeSymbol() {
        return stringFalseableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getStringNullableTypeSymbol() {
        return stringNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getStringFalseableAndNullableTypeSymbol() {
        return stringFalseableAndNullableTypeSymbol;
    }

    @Override
    public IArrayTypeSymbol getArrayTypeSymbol() {
        return arrayTypeSymbol;
    }

    @Override
    public IArrayTypeSymbol getArrayFalseableTypeSymbol() {
        return arrayFalseableTypeSymbol;
    }

    @Override
    public IPseudoTypeSymbol getResourceTypeSymbol() {
        return resourceTypeSymbol;
    }

    @Override
    public IPseudoTypeSymbol getResourceFalseableTypeSymbol() {
        return resourceFalseableTypeSymbol;
    }

    @Override
    public IPseudoTypeSymbol getMixedTypeSymbol() {
        return mixedTypeSymbol;
    }

    @Override
    public IClassTypeSymbol getExceptionTypeSymbol() {
        return exceptionTypeSymbol;
    }

    @Override
    public ICastingMethod getStandardCastingMethod(ITypeSymbol formalParameterType) {
        ICastingMethod castingMethod;
        if (formalParameterType instanceof ITypeSymbolWithPHPBuiltInCasting) {
            castingMethod = new BuiltInCastingMethod(astHelper, (ITypeSymbolWithPHPBuiltInCasting) formalParameterType);
        } else {
            castingMethod = new ClassInterfaceCastingMethod(astHelper, formalParameterType);
        }
        return castingMethod;
    }

    private void defineBuiltInTypes() {

        nullTypeSymbol = symbolFactory.createNullTypeSymbol();
        globalDefaultNamespace.define(nullTypeSymbol);
        voidTypeSymbol = symbolFactory.createVoidTypeSymbol();
        globalDefaultNamespace.define(voidTypeSymbol);

        defineMixedTypeSymbol();

        boolTypeSymbol = defineScalarType("bool", TypeBool, Bool, "false");
        boolFalseableTypeSymbol = defineFalseableScalarType("bool!", TypeBool, Bool);
        boolNullableTypeSymbol = defineNullableScalarType("bool?", TypeBool, Bool);
        boolFalseableAndNullableTypeSymbol = defineFalseableAndNullableScalarType("bool!?", TypeBool, Bool);

        intTypeSymbol = defineScalarType("int", TypeInt, Int, "0");
        intFalseableTypeSymbol = defineFalseableScalarType("int!", TypeInt, Int);
        intNullableTypeSymbol = defineNullableScalarType("int?", TypeInt, Int);
        intFalseableAndNullableTypeSymbol = defineFalseableAndNullableScalarType("int!?", TypeInt, Int);

        floatTypeSymbol = defineScalarType("float", TypeFloat, Float, "0.0");
        floatFalseableTypeSymbol = defineFalseableScalarType("float!", TypeFloat, Float);
        floatNullableTypeSymbol = defineNullableScalarType("float?", TypeFloat, Float);
        floatFalseableAndNullableTypeSymbol = defineFalseableAndNullableScalarType("float!?", TypeFloat, Float);

        stringTypeSymbol = defineScalarType("string", TypeString, String, "''");
        stringFalseableTypeSymbol = defineFalseableScalarType("string!", TypeString, String);
        stringNullableTypeSymbol = defineNullableScalarType("string?", TypeString, String);
        stringFalseableAndNullableTypeSymbol = defineFalseableAndNullableScalarType("string!?", TypeString, String);

        arrayTypeSymbol = symbolFactory.createArrayTypeSymbol("array", TypeArray, stringTypeSymbol, mixedTypeSymbol);
        globalDefaultNamespace.define(arrayTypeSymbol);

        arrayFalseableTypeSymbol = symbolFactory.createArrayTypeSymbol("array!", TypeArray, stringTypeSymbol,
                mixedTypeSymbol);
        arrayFalseableTypeSymbol.addModifier(LogicNot);
        globalDefaultNamespace.define(arrayFalseableTypeSymbol);

        resourceTypeSymbol = symbolFactory.createPseudoTypeSymbol("resource");
        globalDefaultNamespace.define(resourceTypeSymbol);

        resourceFalseableTypeSymbol = symbolFactory.createPseudoTypeSymbol("resource!");
        resourceFalseableTypeSymbol.addModifier(LogicNot);
        globalDefaultNamespace.define(resourceFalseableTypeSymbol);

        //predefined classes
        exceptionTypeSymbol = createClass("Exception");
        defineMethodWithoutParameters(exceptionTypeSymbol, "getMessage()", stringNullableTypeSymbol, true);

        //TODO rstoll TSPHP-401 Define predefined Exceptions
        globalDefaultNamespace.define(exceptionTypeSymbol);

        IClassTypeSymbol errorException = createClass("ErrorException");
        errorException.setParent(exceptionTypeSymbol);
        errorException.addParentTypeSymbol(exceptionTypeSymbol);
        globalDefaultNamespace.define(errorException);
    }

    protected void defineMixedTypeSymbol() {
        mixedTypeSymbol = symbolFactory.createPseudoTypeSymbol("mixed");
        mixedTypeSymbol.addModifier(LogicNot);
        symbolFactory.setMixedTypeSymbol(mixedTypeSymbol);
        globalDefaultNamespace.define(mixedTypeSymbol);
    }

    private IScalarTypeSymbol defineScalarType(
            String name, int tokenType, int defaultValueTokenType, String defaultValue) {
        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(mixedTypeSymbol);
        IScalarTypeSymbol typeSymbol = symbolFactory.createScalarTypeSymbol(
                name, tokenType, parentTypes, defaultValueTokenType, defaultValue);
        globalDefaultNamespace.define(typeSymbol);
        return typeSymbol;
    }

    private IScalarTypeSymbol defineFalseableScalarType(
            String name, int tokenType, int defaultValueTokenType) {
        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(mixedTypeSymbol);
        IScalarTypeSymbol typeSymbol = symbolFactory.createScalarTypeSymbol(
                name, tokenType, parentTypes, defaultValueTokenType, "false");
        typeSymbol.addModifier(LogicNot);
        globalDefaultNamespace.define(typeSymbol);
        return typeSymbol;
    }

    private IScalarTypeSymbol defineNullableScalarType(
            String name, int tokenType, int defaultValueTokenType) {
        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(mixedTypeSymbol);
        IScalarTypeSymbol typeSymbol = symbolFactory.createScalarTypeSymbol(
                name, tokenType, parentTypes, defaultValueTokenType, "null");
        typeSymbol.addModifier(QuestionMark);
        globalDefaultNamespace.define(typeSymbol);
        return typeSymbol;
    }

    private IScalarTypeSymbol defineFalseableAndNullableScalarType(
            String name, int tokenType, int defaultValueTokenType) {
        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(mixedTypeSymbol);
        IScalarTypeSymbol typeSymbol = symbolFactory.createScalarTypeSymbol(
                name, tokenType, parentTypes, defaultValueTokenType, "null");
        typeSymbol.addModifier(LogicNot);
        typeSymbol.addModifier(QuestionMark);
        globalDefaultNamespace.define(typeSymbol);
        return typeSymbol;
    }

    private IClassTypeSymbol createClass(String className) {
        ITSPHPAst classModifier = astHelper.createAst(CLASS_MODIFIER, "cMod");
        classModifier.addChild(astHelper.createAst(TSPHPDefinitionWalker.QuestionMark, "?"));
        ITSPHPAst identifier = astHelper.createAst(TYPE_NAME, className);
        return symbolFactory.createClassTypeSymbol(classModifier, identifier, globalDefaultNamespace);
    }

    private void defineMethodWithoutParameters(IClassTypeSymbol classTypeSymbol, String methodName,
            ITypeSymbol returnType, boolean isNullable) {
        ITSPHPAst methodModifier = astHelper.createAst(METHOD_MODIFIER, "mMod");
        methodModifier.addChild(astHelper.createAst(Public, "public"));
        ITSPHPAst returnTypeModifier = astHelper.createAst(TYPE_MODIFIER, "tMod");
        if (isNullable) {
            returnTypeModifier.addChild(astHelper.createAst(QuestionMark, "?"));
        }
        ITSPHPAst identifier = astHelper.createAst(Identifier, methodName);
        IMethodSymbol methodSymbol = symbolFactory.createMethodSymbol(
                methodModifier, returnTypeModifier, identifier, classTypeSymbol);
        methodSymbol.setType(returnType);
        classTypeSymbol.define(methodSymbol);
    }

    private void initMaps() {
        int[] unaryOperatorTypes = new int[]{
                PRE_INCREMENT, PRE_DECREMENT,
                At, BitwiseNot, LogicNot,
                UNARY_MINUS, UNARY_PLUS,
                POST_INCREMENT, POST_DECREMENT
        };
        for (int unaryOperatorType : unaryOperatorTypes) {
            unaryOperators.put(unaryOperatorType, new ArrayList<IMethodSymbol>());
        }

        int[] binaryOperatorTypes = new int[]{
                LogicOrWeak, LogicXorWeak,
                LogicAndWeak,
                Assign, PlusAssign, MinusAssign,
                MultiplyAssign, DivideAssign,
                BitwiseAndAssign, BitwiseOrAssign,
                BitwiseXorAssign,
                ModuloAssign, DotAssign,
                ShiftLeftAssign, ShiftRightAssign, CAST_ASSIGN,
                LogicOr, LogicAnd,
                BitwiseOr, BitwiseAnd, BitwiseXor,
                Equal, Identical, NotEqual,
                NotIdentical,
                LessThan, LessEqualThan,
                GreaterThan, GreaterEqualThan,
                ShiftLeft, ShiftRight,
                Plus, Minus, Multiply,
                Divide, Modulo, Dot,
                CAST
        };
        for (int binaryOperatorType : binaryOperatorTypes) {
            binaryOperators.put(binaryOperatorType, new ArrayList<IMethodSymbol>());
        }
    }

    private void defineOperators() {

        defineLogicOperators();
        defineBitLevelOperators();
        defineRelationalOperators();
        defineArithmeticOperators();
        defineDotOperator();
    }

    private void defineLogicOperators() {
        Object[][] operators = new Object[][]{
                {"or", LogicOrWeak},
                {"xor", LogicXorWeak},
                {"and", LogicAndWeak},
                {"&&", LogicAnd},
                {"||", LogicOr}
        };
        for (Object[] operator : operators) {
            addToBinaryOperators(operator, boolTypeSymbol, boolTypeSymbol);
            addToBinaryOperators(operator, boolFalseableTypeSymbol, boolTypeSymbol);
        }

        addToUnaryOperators(new Object[]{"!", LogicNot}, boolTypeSymbol, boolTypeSymbol);
        addToUnaryOperators(new Object[]{"!", LogicNot}, boolFalseableTypeSymbol, boolTypeSymbol);
    }


    private void addToBinaryOperators(Object[] operator, ITypeSymbol formalParameterType, ITypeSymbol returnType) {
        addToBinaryOperators(operator, formalParameterType, formalParameterType, returnType);
    }

    private void addToBinaryOperators(
            Object[] operator, ITypeSymbol leftParameterType, ITypeSymbol rightParameterType, ITypeSymbol returnType) {
        IMethodSymbol methodSymbol = createBuiltInMethodSymbol((String) operator[0]);
        methodSymbol.addParameter(createParameter("left", leftParameterType));
        methodSymbol.addParameter(createParameter("right", rightParameterType));
        methodSymbol.setType(returnType);
        addToBinaryOperators((int) operator[1], methodSymbol);
    }

    private void addToBinaryOperators(int operatorType, IMethodSymbol methodSymbol) {
        List<IMethodSymbol> methods = binaryOperators.get(operatorType);
        methods.add(methodSymbol);
    }

    private IMethodSymbol createBuiltInMethodSymbol(String methodName) {
        ITSPHPAst methodModifier = astHelper.createAst(METHOD_MODIFIER, "mMod");
        methodModifier.addChild(astHelper.createAst(Public, "public"));
        ITSPHPAst identifier = astHelper.createAst(Identifier, methodName);
        ITSPHPAst returnTypeModifier = astHelper.createAst(TYPE_MODIFIER, "rtMod");
        return symbolFactory.createMethodSymbol(methodModifier, returnTypeModifier, identifier, globalDefaultNamespace);
    }

    private IVariableSymbol createParameter(String parameterName, ITypeSymbol type) {
        return createParameter(parameterName, type, null);
    }

    private IVariableSymbol createParameter(
            String parameterName, ITypeSymbol type, Map<Integer, String> typeModifiers) {

        ITSPHPAst typeModifier = astHelper.createAst(TYPE_MODIFIER, "tMod");
        addModifiers(typeModifier, typeModifiers);
        ITSPHPAst variableId = astHelper.createAst(VariableId, parameterName);
        IVariableSymbol variableSymbol = symbolFactory.createVariableSymbol(typeModifier, variableId);
        variableSymbol.setType(type);
        return variableSymbol;
    }

    private void addModifiers(ITSPHPAst modifierAst, Map<Integer, String> modifiers) {
        if (modifiers != null) {
            for (Map.Entry<Integer, String> entry : modifiers.entrySet()) {
                modifierAst.addChild(astHelper.createAst(entry.getKey(), entry.getValue()));
            }
        }
    }

    private void addToUnaryOperators(Object[] operator, ITypeSymbol formalParameterType, ITypeSymbol returnType) {
        IMethodSymbol methodSymbol = createBuiltInMethodSymbol((String) operator[0]);
        methodSymbol.addParameter(createParameter("expr", formalParameterType));
        methodSymbol.setType(returnType);
        addToUnaryOperators((int) operator[1], methodSymbol);
    }

    private void addToUnaryOperators(int operatorType, IMethodSymbol methodSymbol) {
        List<IMethodSymbol> methods = unaryOperators.get(operatorType);
        methods.add(methodSymbol);
    }

    private void defineBitLevelOperators() {
        Object[][] operators = new Object[][]{
                {"|", BitwiseOr},
                {"^", BitwiseXor},
                {"&", BitwiseAnd},
                {"<<", ShiftLeft},
                {">>", ShiftRight}
        };
        for (Object[] operator : operators) {
            //bool
            addToBinaryOperators(operator, boolTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolTypeSymbol, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            //bool!
            addToBinaryOperators(operator, boolFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, boolNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            //bool?
            addToBinaryOperators(operator, boolNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolNullableTypeSymbol, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolNullableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            //bool!?
            addToBinaryOperators(operator, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            //int
            addToBinaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            //int!
            addToBinaryOperators(operator, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            //int?
            addToBinaryOperators(operator, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intNullableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            //int!?
            addToBinaryOperators(operator, intFalseableAndNullableTypeSymbol, intTypeSymbol);
        }

        addToUnaryOperators(new Object[]{"~", BitwiseNot}, boolTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Object[]{"~", BitwiseNot}, boolFalseableTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Object[]{"~", BitwiseNot}, boolNullableTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Object[]{"~", BitwiseNot}, boolFalseableAndNullableTypeSymbol, intTypeSymbol);

        addToUnaryOperators(new Object[]{"~", BitwiseNot}, intTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Object[]{"~", BitwiseNot}, intFalseableTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Object[]{"~", BitwiseNot}, intNullableTypeSymbol, intTypeSymbol);
        addToUnaryOperators(new Object[]{"~", BitwiseNot}, intFalseableAndNullableTypeSymbol, intTypeSymbol);
    }

    private void addToBinaryOperatorsInclReversed(
            Object[] operator, ITypeSymbol parameterType1, ITypeSymbol parameterType2, ITypeSymbol returnType) {
        addToBinaryOperators(operator, parameterType1, parameterType2, returnType);
        addToBinaryOperators(operator, parameterType2, parameterType1, returnType);
    }

    private void defineRelationalOperators() {
        Object[][] operators = new Object[][]{
                {"<", LessThan},
                {"<=", LessEqualThan},
                {">", GreaterThan},
                {">=", GreaterEqualThan}
        };

        for (Object[] operator : operators) {
            //int
            addToBinaryOperators(operator, intTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intFalseableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intTypeSymbol, intFalseableAndNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatFalseableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intTypeSymbol, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);

            //int!
            addToBinaryOperators(operator, intFalseableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, intNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatFalseableTypeSymbol,
                    boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatNullableTypeSymbol,
                    boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);

            //int?
            addToBinaryOperators(operator, intNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intNullableTypeSymbol, intFalseableAndNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatFalseableTypeSymbol,
                    boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);

            //int!?
            addToBinaryOperators(operator, intFalseableAndNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);

            //float
            addToBinaryOperators(operator, floatTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatFalseableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, floatTypeSymbol, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);

            //float!
            addToBinaryOperators(operator, floatFalseableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, floatFalseableTypeSymbol, floatNullableTypeSymbol,
                    boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, floatFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);

            //float?
            addToBinaryOperators(operator, floatNullableTypeSymbol, boolTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, floatNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);

            //float!?
            addToBinaryOperators(operator, floatFalseableAndNullableTypeSymbol, boolTypeSymbol);
        }
    }


    private void defineArithmeticOperators() {
        Object[][] operators = new Object[][]{
                {"+", Plus},
                {"-", Minus},
                {"*", Multiply},
                //TODO rstoll TSPHP-819 PHP's int division should yield a falseable int
                {"/", Divide},
                {"%", Modulo}
        };

        for (Object[] operator : operators) {
            //bool
            addToBinaryOperators(operator, boolTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolTypeSymbol, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatFalseableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatNullableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //bool!
            addToBinaryOperators(operator, boolFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, boolNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatFalseableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatNullableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //bool?
            addToBinaryOperators(operator, boolNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolNullableTypeSymbol, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolNullableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatFalseableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatNullableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //bool!?
            addToBinaryOperators(operator, boolFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, floatNullableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, boolFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //int
            addToBinaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatFalseableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatNullableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //int!
            addToBinaryOperators(operator, intFalseableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatFalseableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatNullableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //int?
            addToBinaryOperators(operator, intNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intNullableTypeSymbol, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatFalseableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatNullableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //int!?
            addToBinaryOperators(operator, intFalseableAndNullableTypeSymbol, intTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatNullableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, intFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //float
            addToBinaryOperators(operator, floatTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatFalseableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatNullableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, floatTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //float!
            addToBinaryOperators(operator, floatFalseableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(operator, floatFalseableTypeSymbol, floatNullableTypeSymbol,
                    floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, floatFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //float?
            addToBinaryOperators(operator, floatNullableTypeSymbol, floatTypeSymbol);
            addToBinaryOperatorsInclReversed(
                    operator, floatNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

            //float!?
            addToBinaryOperators(operator, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);

        }

        operators = new Object[][]{
                {"++", PRE_INCREMENT},
                {"++", POST_INCREMENT},
                {"--", PRE_DECREMENT},
                {"--", POST_DECREMENT},
        };
        for (Object[] operator : operators) {
            //todo rstoll TSPHP-732 disallow increment, decrement for bool
            addToUnaryOperators(operator, boolTypeSymbol, boolTypeSymbol);
            addToUnaryOperators(operator, boolFalseableTypeSymbol, boolTypeSymbol);
            addToUnaryOperators(operator, boolNullableTypeSymbol, boolTypeSymbol);
            addToUnaryOperators(operator, boolFalseableAndNullableTypeSymbol, boolTypeSymbol);

            addToUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intFalseableTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intNullableTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            addToUnaryOperators(operator, floatTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, floatFalseableTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, floatNullableTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);
        }

        operators = new Object[][]{
                {"-", UNARY_MINUS},
                {"+", UNARY_PLUS}
        };
        for (Object[] operator : operators) {
            addToUnaryOperators(operator, boolTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, boolFalseableTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, boolNullableTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, boolFalseableAndNullableTypeSymbol, intTypeSymbol);

            addToUnaryOperators(operator, intTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intFalseableTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intNullableTypeSymbol, intTypeSymbol);
            addToUnaryOperators(operator, intFalseableAndNullableTypeSymbol, intTypeSymbol);

            addToUnaryOperators(operator, floatTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, floatFalseableTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, floatNullableTypeSymbol, floatTypeSymbol);
            addToUnaryOperators(operator, floatFalseableAndNullableTypeSymbol, floatTypeSymbol);
        }

        addToBinaryOperators(new Object[]{"+", Plus}, arrayTypeSymbol, arrayTypeSymbol);
    }

    private void defineDotOperator() {
        Object[] operator = new Object[]{".", Dot};

        //bool
        addToBinaryOperators(operator, boolTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, boolFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //bool!
        addToBinaryOperators(operator, boolFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, boolNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatNullableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, stringFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, stringNullableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //bool?
        addToBinaryOperators(operator, boolNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, boolFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatNullableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, stringFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, stringNullableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //bool!?
        addToBinaryOperators(operator, boolFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //int
        addToBinaryOperators(operator, intTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intTypeSymbol, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //int!
        addToBinaryOperators(operator, intFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, intNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatNullableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, stringFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, stringNullableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //int?
        addToBinaryOperators(operator, intNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intNullableTypeSymbol, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, stringFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //int!?
        addToBinaryOperators(operator, intFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //float
        addToBinaryOperators(operator, floatTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //float!
        addToBinaryOperators(operator, floatFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatFalseableTypeSymbol, floatNullableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatFalseableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //float?
        addToBinaryOperators(operator, floatNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatNullableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatNullableTypeSymbol, stringFalseableTypeSymbol,
                stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, floatNullableTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //float!?
        addToBinaryOperators(operator, floatFalseableAndNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //string
        addToBinaryOperators(operator, stringTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, stringTypeSymbol, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(operator, stringTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, stringTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //string!
        addToBinaryOperators(operator, stringFalseableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, stringFalseableTypeSymbol, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, stringFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);

        //string?
        addToBinaryOperators(operator, stringNullableTypeSymbol, stringTypeSymbol);
        addToBinaryOperatorsInclReversed(
                operator, stringNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);
        //string!?
        addToBinaryOperators(operator, stringFalseableAndNullableTypeSymbol, stringTypeSymbol);
    }

    private void defineImplicitCastings() {
        ITypeSymbolWithPHPBuiltInCasting[][] castings = new ITypeSymbolWithPHPBuiltInCasting[][]{
                //bool to bool
                {boolTypeSymbol, boolFalseableTypeSymbol},
                {boolTypeSymbol, boolNullableTypeSymbol},
                {boolTypeSymbol, boolFalseableAndNullableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableTypeSymbol, boolTypeSymbol},
                //{boolFalseableTypeSymbol, boolNullableTypeSymbol},
                {boolFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {boolNullableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                //bool to int
                {boolTypeSymbol, intTypeSymbol},
                {boolTypeSymbol, intFalseableTypeSymbol},
                {boolTypeSymbol, intNullableTypeSymbol},
                {boolTypeSymbol, intFalseableAndNullableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableTypeSymbol, intTypeSymbol},
                {boolFalseableTypeSymbol, intFalseableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableTypeSymbol, intNullableTypeSymbol},
                {boolFalseableTypeSymbol, intFalseableAndNullableTypeSymbol},
                {boolNullableTypeSymbol, intNullableTypeSymbol},
                {boolNullableTypeSymbol, intFalseableAndNullableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableAndNullableTypeSymbol, intNullableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, intFalseableAndNullableTypeSymbol},
                //bool to float
                {boolTypeSymbol, floatTypeSymbol},
                {boolTypeSymbol, floatFalseableTypeSymbol},
                {boolTypeSymbol, floatNullableTypeSymbol},
                {boolTypeSymbol, floatFalseableAndNullableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableTypeSymbol, floatTypeSymbol},
                {boolFalseableTypeSymbol, floatFalseableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableTypeSymbol, floatNullableTypeSymbol},
                {boolFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {boolNullableTypeSymbol, floatNullableTypeSymbol},
                {boolNullableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableAndNullableTypeSymbol, floatNullableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                //bool to string
                {boolTypeSymbol, stringTypeSymbol},
                {boolTypeSymbol, stringFalseableTypeSymbol},
                {boolTypeSymbol, stringNullableTypeSymbol},
                {boolTypeSymbol, stringFalseableAndNullableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableTypeSymbol, stringTypeSymbol},
                {boolFalseableTypeSymbol, stringFalseableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableTypeSymbol, stringNullableTypeSymbol},
                {boolFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {boolNullableTypeSymbol, stringNullableTypeSymbol},
                {boolNullableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                //to be consistent, we also do not support this implicit cast even thought bool and bool! is the same
                //{boolFalseableAndNullableTypeSymbol, stringNullableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                //int to int
                {intTypeSymbol, intFalseableTypeSymbol},
                {intTypeSymbol, intNullableTypeSymbol},
                {intTypeSymbol, intFalseableAndNullableTypeSymbol},
                {intFalseableTypeSymbol, intFalseableAndNullableTypeSymbol},
                {intNullableTypeSymbol, intFalseableAndNullableTypeSymbol},
                //int to float
                {intTypeSymbol, floatTypeSymbol},
                {intTypeSymbol, floatFalseableTypeSymbol},
                {intTypeSymbol, floatNullableTypeSymbol},
                {intTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {intFalseableTypeSymbol, floatFalseableTypeSymbol},
                {intFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {intNullableTypeSymbol, floatNullableTypeSymbol},
                {intNullableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                //int to string
                {intTypeSymbol, stringTypeSymbol},
                {intTypeSymbol, stringFalseableTypeSymbol},
                {intTypeSymbol, stringNullableTypeSymbol},
                {intTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {intFalseableTypeSymbol, stringFalseableTypeSymbol},
                {intFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {intNullableTypeSymbol, stringNullableTypeSymbol},
                {intNullableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                //float to float
                {floatTypeSymbol, floatFalseableTypeSymbol},
                {floatTypeSymbol, floatNullableTypeSymbol},
                {floatTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {floatFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {floatNullableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                //float to string
                {floatTypeSymbol, stringTypeSymbol},
                {floatTypeSymbol, stringFalseableTypeSymbol},
                {floatTypeSymbol, stringNullableTypeSymbol},
                {floatTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {floatFalseableTypeSymbol, stringFalseableTypeSymbol},
                {floatFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {floatNullableTypeSymbol, stringNullableTypeSymbol},
                {floatNullableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                //string to string
                {stringTypeSymbol, stringFalseableTypeSymbol},
                {stringTypeSymbol, stringNullableTypeSymbol},
                {stringTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {stringFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol},
                {stringNullableTypeSymbol, stringFalseableAndNullableTypeSymbol},
        };

        for (ITypeSymbolWithPHPBuiltInCasting[] fromTo : castings) {
            addToCastings(implicitCastings, fromTo[0], fromTo[1], new BuiltInCastingMethod(astHelper, fromTo[1]));
        }
    }

    private void defineExplicitCastings() {
        ITypeSymbol[][] castings = new ITypeSymbol[][]{
                //everything can be casted to bool and array
                {mixedTypeSymbol, boolTypeSymbol},
                {mixedTypeSymbol, boolFalseableTypeSymbol},
                {mixedTypeSymbol, boolNullableTypeSymbol},
                {mixedTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {mixedTypeSymbol, arrayTypeSymbol},
                {mixedTypeSymbol, arrayFalseableTypeSymbol},
                //bool to bool
                {boolFalseableTypeSymbol, boolTypeSymbol},
                {boolFalseableTypeSymbol, boolNullableTypeSymbol},
                {boolNullableTypeSymbol, boolTypeSymbol},
                {boolNullableTypeSymbol, boolFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, boolTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, boolFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, boolNullableTypeSymbol},
                //bool to int
                {boolFalseableTypeSymbol, intTypeSymbol},
                {boolFalseableTypeSymbol, intNullableTypeSymbol},
                {boolNullableTypeSymbol, intTypeSymbol},
                {boolNullableTypeSymbol, intFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, intTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, intFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, intNullableTypeSymbol},
                //bool to float
                {boolFalseableTypeSymbol, floatTypeSymbol},
                {boolFalseableTypeSymbol, floatNullableTypeSymbol},
                {boolNullableTypeSymbol, floatTypeSymbol},
                {boolNullableTypeSymbol, floatFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, floatTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, floatNullableTypeSymbol},
                //bool to string
                {boolFalseableTypeSymbol, stringTypeSymbol},
                {boolFalseableTypeSymbol, stringNullableTypeSymbol},
                {boolNullableTypeSymbol, stringTypeSymbol},
                {boolNullableTypeSymbol, stringFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, stringTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol},
                {boolFalseableAndNullableTypeSymbol, stringNullableTypeSymbol},
                //int to bool
                {intTypeSymbol, boolTypeSymbol},
                {intTypeSymbol, boolFalseableTypeSymbol},
                {intTypeSymbol, boolNullableTypeSymbol},
                {intTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {intFalseableTypeSymbol, boolTypeSymbol},
                {intFalseableTypeSymbol, boolFalseableTypeSymbol},
                {intFalseableTypeSymbol, boolNullableTypeSymbol},
                {intFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {intNullableTypeSymbol, boolTypeSymbol},
                {intNullableTypeSymbol, boolFalseableTypeSymbol},
                {intNullableTypeSymbol, boolNullableTypeSymbol},
                {intNullableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, boolTypeSymbol},
                {intFalseableAndNullableTypeSymbol, boolFalseableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, boolNullableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                //int to int
                {intFalseableTypeSymbol, intTypeSymbol},
                {intFalseableTypeSymbol, intNullableTypeSymbol},
                {intNullableTypeSymbol, intTypeSymbol},
                {intNullableTypeSymbol, intFalseableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, intTypeSymbol},
                {intFalseableAndNullableTypeSymbol, intFalseableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, intNullableTypeSymbol},
                //int to float
                {intFalseableTypeSymbol, floatTypeSymbol},
                {intFalseableTypeSymbol, floatNullableTypeSymbol},
                {intNullableTypeSymbol, floatTypeSymbol},
                {intNullableTypeSymbol, floatFalseableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, floatTypeSymbol},
                {intFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, floatNullableTypeSymbol},
                //int to string
                {intFalseableTypeSymbol, stringTypeSymbol},
                {intFalseableTypeSymbol, stringNullableTypeSymbol},
                {intNullableTypeSymbol, stringTypeSymbol},
                {intNullableTypeSymbol, stringFalseableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, stringTypeSymbol},
                {intFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol},
                {intFalseableAndNullableTypeSymbol, stringNullableTypeSymbol},
                //float to bool
                {floatTypeSymbol, boolTypeSymbol},
                {floatTypeSymbol, boolFalseableTypeSymbol},
                {floatTypeSymbol, boolNullableTypeSymbol},
                {floatTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {floatFalseableTypeSymbol, boolTypeSymbol},
                {floatFalseableTypeSymbol, boolFalseableTypeSymbol},
                {floatFalseableTypeSymbol, boolNullableTypeSymbol},
                {floatFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {floatNullableTypeSymbol, boolTypeSymbol},
                {floatNullableTypeSymbol, boolFalseableTypeSymbol},
                {floatNullableTypeSymbol, boolNullableTypeSymbol},
                {floatNullableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, boolTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, boolFalseableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, boolNullableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                //float to int
                {floatTypeSymbol, intTypeSymbol},
                {floatTypeSymbol, intFalseableTypeSymbol},
                {floatTypeSymbol, intNullableTypeSymbol},
                {floatTypeSymbol, intFalseableAndNullableTypeSymbol},
                {floatFalseableTypeSymbol, intTypeSymbol},
                {floatFalseableTypeSymbol, intFalseableTypeSymbol},
                {floatFalseableTypeSymbol, intNullableTypeSymbol},
                {floatFalseableTypeSymbol, intFalseableAndNullableTypeSymbol},
                {floatNullableTypeSymbol, intTypeSymbol},
                {floatNullableTypeSymbol, intFalseableTypeSymbol},
                {floatNullableTypeSymbol, intNullableTypeSymbol},
                {floatNullableTypeSymbol, intFalseableAndNullableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, intTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, intFalseableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, intNullableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, intFalseableAndNullableTypeSymbol},
                //float to float
                {floatFalseableTypeSymbol, floatTypeSymbol},
                {floatFalseableTypeSymbol, floatNullableTypeSymbol},
                {floatNullableTypeSymbol, floatTypeSymbol},
                {floatNullableTypeSymbol, floatFalseableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, floatTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, floatNullableTypeSymbol},
                //float to string
                {floatFalseableTypeSymbol, stringTypeSymbol},
                {floatFalseableTypeSymbol, stringNullableTypeSymbol},
                {floatNullableTypeSymbol, stringTypeSymbol},
                {floatNullableTypeSymbol, stringFalseableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, stringTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol},
                {floatFalseableAndNullableTypeSymbol, stringNullableTypeSymbol},
                //string to bool
                {stringTypeSymbol, boolTypeSymbol},
                {stringTypeSymbol, boolFalseableTypeSymbol},
                {stringTypeSymbol, boolNullableTypeSymbol},
                {stringTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {stringFalseableTypeSymbol, boolTypeSymbol},
                {stringFalseableTypeSymbol, boolFalseableTypeSymbol},
                {stringFalseableTypeSymbol, boolNullableTypeSymbol},
                {stringFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {stringNullableTypeSymbol, boolTypeSymbol},
                {stringNullableTypeSymbol, boolFalseableTypeSymbol},
                {stringNullableTypeSymbol, boolNullableTypeSymbol},
                {stringNullableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, boolTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, boolFalseableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, boolNullableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, boolFalseableAndNullableTypeSymbol},
                //string to int
                {stringTypeSymbol, intTypeSymbol},
                {stringTypeSymbol, intFalseableTypeSymbol},
                {stringTypeSymbol, intNullableTypeSymbol},
                {stringTypeSymbol, intFalseableAndNullableTypeSymbol},
                {stringFalseableTypeSymbol, intTypeSymbol},
                {stringFalseableTypeSymbol, intFalseableTypeSymbol},
                {stringFalseableTypeSymbol, intNullableTypeSymbol},
                {stringFalseableTypeSymbol, intFalseableAndNullableTypeSymbol},
                {stringNullableTypeSymbol, intTypeSymbol},
                {stringNullableTypeSymbol, intFalseableTypeSymbol},
                {stringNullableTypeSymbol, intNullableTypeSymbol},
                {stringNullableTypeSymbol, intFalseableAndNullableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, intTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, intFalseableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, intNullableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, intFalseableAndNullableTypeSymbol},
                //string to float
                {stringTypeSymbol, floatTypeSymbol},
                {stringTypeSymbol, floatFalseableTypeSymbol},
                {stringTypeSymbol, floatNullableTypeSymbol},
                {stringTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {stringFalseableTypeSymbol, floatTypeSymbol},
                {stringFalseableTypeSymbol, floatFalseableTypeSymbol},
                {stringFalseableTypeSymbol, floatNullableTypeSymbol},
                {stringFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {stringNullableTypeSymbol, floatTypeSymbol},
                {stringNullableTypeSymbol, floatFalseableTypeSymbol},
                {stringNullableTypeSymbol, floatNullableTypeSymbol},
                {stringNullableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, floatTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, floatNullableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol},
                //string to string
                {stringFalseableTypeSymbol, stringTypeSymbol},
                {stringFalseableTypeSymbol, stringNullableTypeSymbol},
                {stringNullableTypeSymbol, stringTypeSymbol},
                {stringNullableTypeSymbol, stringFalseableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, stringTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol},
                {stringFalseableAndNullableTypeSymbol, stringNullableTypeSymbol},
        };

        for (ITypeSymbol[] fromTo : castings) {
            addToCastings(explicitCastings, fromTo[0], fromTo[1],
                    new BuiltInCastingMethod(astHelper, (ITypeSymbolWithPHPBuiltInCasting) fromTo[1]));
        }
    }


    @Override
    public void addExplicitCastFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        addExplicitCastFromTo(actualParameterType, formalParameterType, getStandardCastingMethod(formalParameterType));
    }

    @Override
    public void addExplicitCastFromTo(
            ITypeSymbol actualParameterType, ITypeSymbol formalParameterType, ICastingMethod castingMethod) {
        addToCastings(explicitCastings, actualParameterType, formalParameterType, castingMethod);
    }

    @Override
    public void addImplicitCastFromTo(ITypeSymbol actualParameterType, ITypeSymbol formalParameterType) {
        addImplicitCastFromTo(actualParameterType, formalParameterType, getStandardCastingMethod(formalParameterType));
    }

    @Override
    public void addImplicitCastFromTo(
            ITypeSymbol actualParameterType, ITypeSymbol formalParameterType, ICastingMethod castingMethod) {
        addToCastings(implicitCastings, actualParameterType, formalParameterType, castingMethod);
    }

    private void addToCastings(Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> castings,
            ITypeSymbol from, ITypeSymbol to, ICastingMethod castingMethod) {
        if (!castings.containsKey(from)) {
            castings.put(from, new HashMap<ITypeSymbol, ICastingMethod>());
        }
        castings.get(from).put(to, castingMethod);
    }
}



