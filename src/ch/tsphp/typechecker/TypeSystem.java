/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
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

public class TypeSystem implements ITypeSystem
{

    private final ISymbolFactory symbolFactory;
    private final IAstHelper astHelper;
    //
    private final Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private final Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();
    private final Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> explicitCastings = new HashMap<>();
    //
    private INullTypeSymbol nullTypeSymbol;
    private IScalarTypeSymbol boolTypeSymbol;
    private IScalarTypeSymbol boolNullableTypeSymbol;
    private IScalarTypeSymbol intTypeSymbol;
    private IScalarTypeSymbol intNullableTypeSymbol;
    private IScalarTypeSymbol floatTypeSymbol;
    private IScalarTypeSymbol floatNullableTypeSymbol;
    private IScalarTypeSymbol stringTypeSymbol;
    private IScalarTypeSymbol stringNullableTypeSymbol;
    private IArrayTypeSymbol arrayTypeSymbol;
    private IPseudoTypeSymbol resourceTypeSymbol;
    private IPseudoTypeSymbol objectTypeSymbol;
    private IClassTypeSymbol exceptionTypeSymbol;
    //
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
    public IScalarTypeSymbol getBoolNullableTypeSymbol() {
        return boolNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getIntTypeSymbol() {
        return intTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getIntNullableTypeSymbol() {
        return intNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getFloatTypeSymbol() {
        return floatTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getFloatNullableTypeSymbol() {
        return floatNullableTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getStringTypeSymbol() {
        return stringTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getStringNullableTypeSymbol() {
        return stringNullableTypeSymbol;
    }

    @Override
    public IArrayTypeSymbol getArrayTypeSymbol() {
        return arrayTypeSymbol;
    }

    @Override
    public IPseudoTypeSymbol getResourceTypeSymbol() {
        return resourceTypeSymbol;
    }

    @Override
    public IPseudoTypeSymbol getObjectTypeSymbol() {
        return objectTypeSymbol;
    }

    @Override
    public IClassTypeSymbol getExceptionTypeSymbol() {
        return exceptionTypeSymbol;
    }

    @Override
    public ICastingMethod getStandardCastingMethod(ITypeSymbol typeSymbol) {
        ICastingMethod castingMethod;
        if (typeSymbol instanceof ITypeSymbolWithPHPBuiltInCasting) {
            castingMethod = new BuiltInCastingMethod(astHelper, (ITypeSymbolWithPHPBuiltInCasting) typeSymbol);
        } else {
            castingMethod = new ClassInterfaceCastingMethod(astHelper, typeSymbol);
        }
        return castingMethod;
    }

    protected void defineObjectTypeSymbol() {
        objectTypeSymbol = symbolFactory.createPseudoTypeSymbol("object");
        symbolFactory.setObjectTypeSymbol(objectTypeSymbol);
        globalDefaultNamespace.define(objectTypeSymbol);
    }

    private void defineBuiltInTypes() {

        nullTypeSymbol = symbolFactory.createNullTypeSymbol();
        globalDefaultNamespace.define(nullTypeSymbol);
        voidTypeSymbol = symbolFactory.createVoidTypeSymbol();
        globalDefaultNamespace.define(voidTypeSymbol);

        defineObjectTypeSymbol();

        stringNullableTypeSymbol = symbolFactory.createScalarTypeSymbol("string?", TSPHPDefinitionWalker.TypeString,
                objectTypeSymbol, true,
                TSPHPDefinitionWalker.Null, "null");
        globalDefaultNamespace.define(stringNullableTypeSymbol);

        floatNullableTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "float?", TSPHPDefinitionWalker.TypeFloat, stringNullableTypeSymbol, true,
                TSPHPDefinitionWalker.Null, "null");
        globalDefaultNamespace.define(floatNullableTypeSymbol);

        intNullableTypeSymbol = symbolFactory.createScalarTypeSymbol("int?", TSPHPDefinitionWalker.TypeInt,
                floatNullableTypeSymbol, true,
                TSPHPDefinitionWalker.Null, "null");
        globalDefaultNamespace.define(intNullableTypeSymbol);

        boolNullableTypeSymbol = symbolFactory.createScalarTypeSymbol("bool?", TSPHPDefinitionWalker.TypeBool,
                intNullableTypeSymbol, true,
                TSPHPDefinitionWalker.Null, "null");
        globalDefaultNamespace.define(boolNullableTypeSymbol);


        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(objectTypeSymbol);
        parentTypes.add(stringNullableTypeSymbol);
        stringTypeSymbol = symbolFactory.createScalarTypeSymbol("string", TSPHPDefinitionWalker.TypeString,
                parentTypes, false,
                TSPHPDefinitionWalker.String, "''");
        globalDefaultNamespace.define(stringTypeSymbol);

        parentTypes = new HashSet<>();
        parentTypes.add(stringTypeSymbol);
        parentTypes.add(floatNullableTypeSymbol);
        floatTypeSymbol = symbolFactory.createScalarTypeSymbol("float", TSPHPDefinitionWalker.TypeFloat, parentTypes,
                false,
                TSPHPDefinitionWalker.Float, "0.0");
        globalDefaultNamespace.define(floatTypeSymbol);

        parentTypes = new HashSet<>();
        parentTypes.add(floatTypeSymbol);
        parentTypes.add(intNullableTypeSymbol);
        intTypeSymbol = symbolFactory.createScalarTypeSymbol("int", TSPHPDefinitionWalker.TypeInt, parentTypes,
                false, TSPHPDefinitionWalker.Int, "0");
        globalDefaultNamespace.define(intTypeSymbol);

        parentTypes = new HashSet<>();
        parentTypes.add(intTypeSymbol);
        parentTypes.add(boolNullableTypeSymbol);
        boolTypeSymbol = symbolFactory.createScalarTypeSymbol("bool", TSPHPDefinitionWalker.TypeBool, parentTypes,
                false, TSPHPDefinitionWalker.Bool, "false");
        globalDefaultNamespace.define(boolTypeSymbol);

        arrayTypeSymbol = symbolFactory.createArrayTypeSymbol("array", TSPHPDefinitionWalker.TypeArray,
                stringTypeSymbol, objectTypeSymbol);
        globalDefaultNamespace.define(arrayTypeSymbol);

        resourceTypeSymbol = symbolFactory.createPseudoTypeSymbol("resource");
        globalDefaultNamespace.define(resourceTypeSymbol);

        //predefiend classes
        exceptionTypeSymbol = createClass("Exception");
        //TODO rstoll TSPHP-401 Define predefined Exceptions
        globalDefaultNamespace.define(exceptionTypeSymbol);

        IClassTypeSymbol errorException = createClass("ErrorException");
        errorException.setParent(exceptionTypeSymbol);
        errorException.addParentTypeSymbol(exceptionTypeSymbol);
        globalDefaultNamespace.define(errorException);
    }

    private IClassTypeSymbol createClass(String className) {
        ITSPHPAst classModifier = astHelper.createAst(TSPHPDefinitionWalker.CLASS_MODIFIER, "cMod");
        ITSPHPAst identifier = astHelper.createAst(TSPHPDefinitionWalker.TYPE_NAME, className);
        return symbolFactory.createClassTypeSymbol(classModifier, identifier, globalDefaultNamespace);
    }

    private void initMaps() {
        int[] unaryOperatorTypes = new int[]{
                TSPHPDefinitionWalker.PRE_INCREMENT, TSPHPDefinitionWalker.PRE_DECREMENT,
                TSPHPDefinitionWalker.At, TSPHPDefinitionWalker.BitwiseNot, TSPHPDefinitionWalker.LogicNot,
                TSPHPDefinitionWalker.UNARY_MINUS, TSPHPDefinitionWalker.UNARY_PLUS,
                TSPHPDefinitionWalker.POST_INCREMENT, TSPHPDefinitionWalker.POST_DECREMENT
        };
        for (int unaryOperatorType : unaryOperatorTypes) {
            unaryOperators.put(unaryOperatorType, new ArrayList<IMethodSymbol>());
        }

        int[] binaryOperatorTypes = new int[]{
                TSPHPDefinitionWalker.LogicOrWeak, TSPHPDefinitionWalker.LogicXorWeak,
                TSPHPDefinitionWalker.LogicAndWeak,
                TSPHPDefinitionWalker.Assign, TSPHPDefinitionWalker.PlusAssign, TSPHPDefinitionWalker.MinusAssign,
                TSPHPDefinitionWalker.MultiplyAssign, TSPHPDefinitionWalker.DivideAssign,
                TSPHPDefinitionWalker.BitwiseAndAssign, TSPHPDefinitionWalker.BitwiseOrAssign,
                TSPHPDefinitionWalker.BitwiseXorAssign,
                TSPHPDefinitionWalker.ModuloAssign, TSPHPDefinitionWalker.DotAssign, TSPHPDefinitionWalker
                .ShiftLeftAssign, TSPHPDefinitionWalker.ShiftRightAssign, TSPHPDefinitionWalker.CAST_ASSIGN,
                TSPHPDefinitionWalker.LogicOr, TSPHPDefinitionWalker.LogicAnd,
                TSPHPDefinitionWalker.BitwiseOr, TSPHPDefinitionWalker.BitwiseAnd, TSPHPDefinitionWalker.BitwiseXor,
                TSPHPDefinitionWalker.Equal, TSPHPDefinitionWalker.Identical, TSPHPDefinitionWalker.NotEqual,
                TSPHPDefinitionWalker.NotIdentical,
                TSPHPDefinitionWalker.LessThan, TSPHPDefinitionWalker.LessEqualThan,
                TSPHPDefinitionWalker.GreaterThan, TSPHPDefinitionWalker.GreaterEqualThan,
                TSPHPDefinitionWalker.ShiftLeft, TSPHPDefinitionWalker.ShiftRight,
                TSPHPDefinitionWalker.Plus, TSPHPDefinitionWalker.Minus, TSPHPDefinitionWalker.Multiply,
                TSPHPDefinitionWalker.Divide, TSPHPDefinitionWalker.Modulo, TSPHPDefinitionWalker.Dot,
                TSPHPDefinitionWalker.CAST
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

        IMethodSymbol methodSymbol = createInBuiltMethodSymbol(".");
        methodSymbol.addParameter(createParameter("left", stringNullableTypeSymbol));
        methodSymbol.addParameter(createParameter("right", stringNullableTypeSymbol));
        methodSymbol.setType(stringTypeSymbol);
        addToBinaryOperators(TSPHPDefinitionWalker.Dot, methodSymbol);

    }

    private IMethodSymbol createInBuiltMethodSymbol(String methodName) {
        ITSPHPAst methodModifier = astHelper.createAst(TSPHPDefinitionWalker.METHOD_MODIFIER, "mMod");
        methodModifier.addChild(astHelper.createAst(TSPHPDefinitionWalker.Public, "public"));
        ITSPHPAst identifier = astHelper.createAst(TSPHPDefinitionWalker.Identifier, methodName);
        ITSPHPAst returnTypeModifier = astHelper.createAst(TSPHPDefinitionWalker.TYPE_MODIFIER, "rtMod");
        return symbolFactory.createMethodSymbol(methodModifier, returnTypeModifier, identifier, globalDefaultNamespace);

    }

    private IVariableSymbol createParameter(String parameterName, ITypeSymbol type) {
        return createParameter(parameterName, type, null);
    }

    private IVariableSymbol createParameter(String parameterName, ITypeSymbol type,
            Map<Integer, String> typeModifiers) {
        ITSPHPAst typeModifier = astHelper.createAst(TSPHPDefinitionWalker.TYPE_MODIFIER, "tMod");
        addModifiers(typeModifier, typeModifiers);
        ITSPHPAst variableId = astHelper.createAst(TSPHPDefinitionWalker.VariableId, parameterName);
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

    private void defineLogicOperators() {
        Object[][] operators = new Object[][]{
                {"or", TSPHPDefinitionWalker.LogicOrWeak},
                {"xor", TSPHPDefinitionWalker.LogicXorWeak},
                {"and", TSPHPDefinitionWalker.LogicAndWeak},
                {"&&", TSPHPDefinitionWalker.LogicAnd},
                {"||", TSPHPDefinitionWalker.LogicOr}
        };
        for (Object[] operator : operators) {
            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", boolTypeSymbol));
            methodSymbol.addParameter(createParameter("right", boolTypeSymbol));
            methodSymbol.setType(boolTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);
        }

        IMethodSymbol methodSymbol = createInBuiltMethodSymbol("!");
        methodSymbol.addParameter(createParameter("expr", boolTypeSymbol));
        methodSymbol.setType(boolTypeSymbol);
        addToUnaryOperators(TSPHPDefinitionWalker.LogicNot, methodSymbol);
    }

    private void defineBitLevelOperators() {
        Object[][] operators = new Object[][]{
                {"|", TSPHPDefinitionWalker.BitwiseOr},
                {"^", TSPHPDefinitionWalker.BitwiseXor},
                {"&", TSPHPDefinitionWalker.BitwiseAnd},
                {"<<", TSPHPDefinitionWalker.ShiftLeft},
                {">>", TSPHPDefinitionWalker.ShiftRight}
        };
        for (Object[] operator : operators) {
            addIntOperator(operator);
        }
        IMethodSymbol methodSymbol = createInBuiltMethodSymbol("~");
        methodSymbol.addParameter(createParameter("expr", intTypeSymbol));
        methodSymbol.setType(intTypeSymbol);
        addToUnaryOperators(TSPHPDefinitionWalker.BitwiseNot, methodSymbol);

        methodSymbol = createInBuiltMethodSymbol("~");
        methodSymbol.addParameter(createParameter("expr", intNullableTypeSymbol));
        methodSymbol.setType(intTypeSymbol);
        addToUnaryOperators(TSPHPDefinitionWalker.BitwiseNot, methodSymbol);
    }

    private void addIntOperator(Object[] operator) {
        IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
        methodSymbol.addParameter(createParameter("left", intTypeSymbol));
        methodSymbol.addParameter(createParameter("right", intTypeSymbol));
        methodSymbol.setType(intTypeSymbol);
        addToBinaryOperators((int) operator[1], methodSymbol);

        methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
        methodSymbol.addParameter(createParameter("left", intNullableTypeSymbol));
        methodSymbol.addParameter(createParameter("right", intNullableTypeSymbol));
        methodSymbol.setType(intTypeSymbol);
        addToBinaryOperators((int) operator[1], methodSymbol);
    }

    private void defineRelationalOperators() {
        Object[][] operators = new Object[][]{
                {"<", TSPHPDefinitionWalker.LessThan},
                {"<=", TSPHPDefinitionWalker.LessEqualThan},
                {">", TSPHPDefinitionWalker.GreaterThan},
                {">=", TSPHPDefinitionWalker.GreaterEqualThan}
        };
        for (Object[] operator : operators) {
            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", floatNullableTypeSymbol));
            methodSymbol.addParameter(createParameter("right", floatNullableTypeSymbol));
            methodSymbol.setType(boolTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);
        }
    }

    private void defineArithmeticOperators() {
        Object[][] operators = new Object[][]{
                {"+", TSPHPDefinitionWalker.Plus},
                {"-", TSPHPDefinitionWalker.Minus},
                {"*", TSPHPDefinitionWalker.Multiply},
                {"/", TSPHPDefinitionWalker.Divide},
                {"%", TSPHPDefinitionWalker.Modulo}
        };

        for (Object[] operator : operators) {
            addIntOperator(operator);

            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", floatTypeSymbol));
            methodSymbol.addParameter(createParameter("right", floatTypeSymbol));
            methodSymbol.setType(floatTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);

            methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", floatNullableTypeSymbol));
            methodSymbol.addParameter(createParameter("right", floatNullableTypeSymbol));
            methodSymbol.setType(floatTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);
        }

        operators = new Object[][]{
                {"++", TSPHPDefinitionWalker.PRE_INCREMENT},
                {"++", TSPHPDefinitionWalker.POST_INCREMENT},
                {"--", TSPHPDefinitionWalker.PRE_DECREMENT},
                {"--", TSPHPDefinitionWalker.POST_DECREMENT},
                {"-", TSPHPDefinitionWalker.UNARY_MINUS},
                {"+", TSPHPDefinitionWalker.UNARY_PLUS}
        };
        for (Object[] operator : operators) {
            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("expr", intTypeSymbol));
            methodSymbol.setType(intTypeSymbol);
            addToUnaryOperators((int) operator[1], methodSymbol);

            methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("expr", intNullableTypeSymbol));
            methodSymbol.setType(intTypeSymbol);
            addToUnaryOperators((int) operator[1], methodSymbol);

            methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("expr", floatTypeSymbol));
            methodSymbol.setType(floatTypeSymbol);
            addToUnaryOperators((int) operator[1], methodSymbol);

            methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("expr", floatNullableTypeSymbol));
            methodSymbol.setType(floatTypeSymbol);
            addToUnaryOperators((int) operator[1], methodSymbol);
        }

        IMethodSymbol methodSymbol = createInBuiltMethodSymbol("+");
        methodSymbol.addParameter(createParameter("left", arrayTypeSymbol));
        methodSymbol.addParameter(createParameter("right", arrayTypeSymbol));
        methodSymbol.setType(arrayTypeSymbol);
        addToBinaryOperators(TSPHPDefinitionWalker.Plus, methodSymbol);

    }

    private void addToUnaryOperators(int operatorType, IMethodSymbol methodSymbol) {
        List<IMethodSymbol> methods = unaryOperators.get(operatorType);
        methods.add(methodSymbol);
    }

    private void addToBinaryOperators(int operatorType, IMethodSymbol methodSymbol) {
        List<IMethodSymbol> methods = binaryOperators.get(operatorType);
        methods.add(methodSymbol);
    }

    private void defineExplicitCastings() {
        ITypeSymbol[][] castings = new ITypeSymbol[][]{
                //everything is castable to bool and array
                {objectTypeSymbol, boolNullableTypeSymbol},
                {objectTypeSymbol, boolTypeSymbol},
                {objectTypeSymbol, arrayTypeSymbol},
                //
                {boolNullableTypeSymbol, intTypeSymbol},
                {boolNullableTypeSymbol, floatTypeSymbol},
                {boolNullableTypeSymbol, stringTypeSymbol},
                {intNullableTypeSymbol, floatTypeSymbol},
                {intNullableTypeSymbol, stringTypeSymbol},
                {floatNullableTypeSymbol, stringTypeSymbol},
                //
                {intTypeSymbol, boolNullableTypeSymbol},
                {floatTypeSymbol, boolNullableTypeSymbol},
                {floatTypeSymbol, intNullableTypeSymbol},
                {stringTypeSymbol, boolNullableTypeSymbol},
                {stringTypeSymbol, intNullableTypeSymbol},
                {stringTypeSymbol, floatNullableTypeSymbol}, //
        };

        for (ITypeSymbol[] fromTo : castings) {
            addToExplicitTypeCastings(fromTo[0], fromTo[1],
                    new BuiltInCastingMethod(astHelper, (ITypeSymbolWithPHPBuiltInCasting) fromTo[1]));
        }
    }

    private void addToExplicitTypeCastings(ITypeSymbol from, ITypeSymbol to, ICastingMethod method) {
        if (!explicitCastings.containsKey(from)) {
            explicitCastings.put(from, new HashMap<ITypeSymbol, ICastingMethod>());
        }
        explicitCastings.get(from).put(to, method);
    }
}
