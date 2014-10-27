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
    private final IGlobalNamespaceScope globalDefaultNamespace;

    private final Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private final Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();
    private final Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> implicitCastings = new HashMap<>();
    private final Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> explicitCastings = new HashMap<>();

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
        defineScalarTypeSymbols();

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

    private void defineScalarTypeSymbols() {
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
            defineBinaryOperatorForBool(operator, intTypeSymbol);
            defineBinaryOperatorForBoolInt(operator, intTypeSymbol);

            //int
            defineBinaryOperatorForInt(operator, intTypeSymbol);
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

    private void defineBinaryOperatorForBool(Object[] operator, ITypeSymbol returnType) {
        //bool
        addToBinaryOperators(operator, boolTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, boolNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, boolFalseableAndNullableTypeSymbol, returnType);
        //bool!
        addToBinaryOperators(operator, boolFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, boolNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, boolFalseableAndNullableTypeSymbol, returnType);
        //bool?
        addToBinaryOperators(operator, boolNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, boolFalseableAndNullableTypeSymbol, returnType);

        //bool!?
        addToBinaryOperators(operator, boolFalseableAndNullableTypeSymbol, returnType);
    }

    private void defineBinaryOperatorForBoolInt(Object[] operator, ITypeSymbol returnType) {
        //bool
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, intNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, intFalseableAndNullableTypeSymbol, returnType);
        //bool!
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, intNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, returnType);
        //bool?
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, intNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, intFalseableAndNullableTypeSymbol, returnType);
        //bool!?
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, intFalseableAndNullableTypeSymbol, returnType);


    }

    private void defineBinaryOperatorForBoolFloat(Object[] operator, ITypeSymbol returnType) {
        //bool
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, floatNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
        //bool!
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, floatNullableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
        //bool?
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, floatNullableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
        //bool!?
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);

    }

    private void defineBinaryOperatorForBoolString(Object[] operator, ITypeSymbol returnType) {
        //bool
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //bool!
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, stringFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, boolFalseableTypeSymbol, stringNullableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //bool?
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, stringFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, boolNullableTypeSymbol, stringNullableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //bool!?
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, boolFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
    }

    private void defineBinaryOperatorForInt(Object[] operator, ITypeSymbol returnType) {
        //int
        addToBinaryOperators(operator, intTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, intNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intTypeSymbol, intFalseableAndNullableTypeSymbol, returnType);
        //int!
        addToBinaryOperators(operator, intFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, intNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableTypeSymbol, intFalseableAndNullableTypeSymbol, returnType);
        //int?
        addToBinaryOperators(operator, intNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intNullableTypeSymbol, intFalseableAndNullableTypeSymbol, returnType);
        //int!?
        addToBinaryOperators(operator, intFalseableAndNullableTypeSymbol, returnType);
    }

    private void defineBinaryOperatorForIntFloat(Object[] operator, ITypeSymbol returnType) {
        //int
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, floatNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
        //int!
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, floatNullableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
        //int?
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, floatNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
        //int!?
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
    }

    private void defineBinaryOperatorForIntString(Object[] operator, ITypeSymbol returnType) {
        //int
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //int!
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, stringFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, intFalseableTypeSymbol, stringNullableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //int?
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, stringFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, intNullableTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //int!?
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, intFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
    }

    private void defineBinaryOperatorForFloat(Object[] operator, ITypeSymbol returnType) {
        //float
        addToBinaryOperators(operator, floatTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, floatNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);
        //float!
        addToBinaryOperators(operator, floatFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, floatFalseableTypeSymbol, floatNullableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);

        //float?
        addToBinaryOperators(operator, floatNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatNullableTypeSymbol, floatFalseableAndNullableTypeSymbol, returnType);

        //float!?
        addToBinaryOperators(operator, floatFalseableAndNullableTypeSymbol, returnType);
    }

    private void defineBinaryOperatorForFloatString(Object[] operator, ITypeSymbol returnType) {
        //float
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, floatTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //float!
        addToBinaryOperatorsInclReversed(operator, floatFalseableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //float?
        addToBinaryOperatorsInclReversed(operator, floatNullableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, floatNullableTypeSymbol, stringFalseableTypeSymbol,
                returnType);
        addToBinaryOperatorsInclReversed(operator, floatNullableTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //float!?
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, floatFalseableAndNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
    }

    private void defineBinaryOperatorForString(Object[] operator, ITypeSymbol returnType) {
        //string
        addToBinaryOperators(operator, stringTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, stringTypeSymbol, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(operator, stringTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, stringTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //string!
        addToBinaryOperators(operator, stringFalseableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, stringFalseableTypeSymbol, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, stringFalseableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //string?
        addToBinaryOperators(operator, stringNullableTypeSymbol, returnType);
        addToBinaryOperatorsInclReversed(
                operator, stringNullableTypeSymbol, stringFalseableAndNullableTypeSymbol, returnType);
        //string!?
        addToBinaryOperators(operator, stringFalseableAndNullableTypeSymbol, returnType);
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
            defineBinaryOperatorForInt(operator, boolTypeSymbol);
            defineBinaryOperatorForIntFloat(operator, boolTypeSymbol);

            //float
            defineBinaryOperatorForFloat(operator, boolTypeSymbol);
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
            defineBinaryOperatorForBool(operator, intTypeSymbol);
            defineBinaryOperatorForBoolInt(operator, intTypeSymbol);
            defineBinaryOperatorForBoolFloat(operator, floatTypeSymbol);

            //int
            defineBinaryOperatorForInt(operator, intTypeSymbol);
            defineBinaryOperatorForIntFloat(operator, floatTypeSymbol);

            //float
            defineBinaryOperatorForFloat(operator, floatTypeSymbol);
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
        defineBinaryOperatorForBool(operator, stringTypeSymbol);
        defineBinaryOperatorForBoolInt(operator, stringTypeSymbol);
        defineBinaryOperatorForBoolFloat(operator, stringTypeSymbol);
        defineBinaryOperatorForBoolString(operator, stringTypeSymbol);

        //int
        defineBinaryOperatorForInt(operator, stringTypeSymbol);
        defineBinaryOperatorForIntFloat(operator, stringTypeSymbol);
        defineBinaryOperatorForIntString(operator, stringTypeSymbol);

        //float
        defineBinaryOperatorForFloat(operator, stringTypeSymbol);
        defineBinaryOperatorForFloatString(operator, stringTypeSymbol);

        //string
        defineBinaryOperatorForString(operator, stringTypeSymbol);
    }

    @SuppressWarnings("checkstyle:methodlength")
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

    @SuppressWarnings("checkstyle:methodlength")
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



