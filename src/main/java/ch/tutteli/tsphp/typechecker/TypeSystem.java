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
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IAstHelper;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.*;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.INullTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPseudoTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVoidTypeSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TypeSystem implements ITypeSystem
{

    private ISymbolFactory symbolFactory;
    private IAstHelper astHelper;
    //
    private Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();
    private Map<ITypeSymbol, Map<ITypeSymbol, ICastingMethod>> explicitCastings = new HashMap<>();
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
    private IGlobalNamespaceScope globalDefaultNamespace;
    private IVoidTypeSymbol voidTypeSymbol;

    public TypeSystem(ISymbolFactory theSymbolFactory, IAstHelper theAstHelper,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {

        symbolFactory = theSymbolFactory;
        astHelper = theAstHelper;
        globalDefaultNamespace = theGlobalDefaultNamespace;
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

    @Override
    public void initTypeSystem() {
        defineBuiltInTypes();
        initMaps();
        defineOperators();
        defineExplicitCastings();
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

        stringNullableTypeSymbol = symbolFactory.createScalarTypeSymbol("string?", TypeString, objectTypeSymbol, true,
                Null, "null");
        globalDefaultNamespace.define(stringNullableTypeSymbol);

        floatNullableTypeSymbol = symbolFactory.createScalarTypeSymbol(
                "float?", TypeFloat, stringNullableTypeSymbol, true, Null, "null");
        globalDefaultNamespace.define(floatNullableTypeSymbol);

        intNullableTypeSymbol = symbolFactory.createScalarTypeSymbol("int?", TypeInt, floatNullableTypeSymbol, true,
                Null, "null");
        globalDefaultNamespace.define(intNullableTypeSymbol);

        boolNullableTypeSymbol = symbolFactory.createScalarTypeSymbol("bool?", TypeBool, intNullableTypeSymbol, true,
                Null, "null");
        globalDefaultNamespace.define(boolNullableTypeSymbol);


        Set<ITypeSymbol> parentTypes = new HashSet<>();
        parentTypes.add(objectTypeSymbol);
        parentTypes.add(stringNullableTypeSymbol);
        stringTypeSymbol = symbolFactory.createScalarTypeSymbol("string", TypeString, parentTypes, false,
                TypeString, "''");
        globalDefaultNamespace.define(stringTypeSymbol);

        parentTypes = new HashSet<>();
        parentTypes.add(stringTypeSymbol);
        parentTypes.add(floatNullableTypeSymbol);
        floatTypeSymbol = symbolFactory.createScalarTypeSymbol("float", TypeFloat, parentTypes, false,
                TypeFloat, "0.0");
        globalDefaultNamespace.define(floatTypeSymbol);

        parentTypes = new HashSet<>();
        parentTypes.add(floatTypeSymbol);
        parentTypes.add(intNullableTypeSymbol);
        intTypeSymbol = symbolFactory.createScalarTypeSymbol("int", TypeInt, parentTypes, false, TypeInt, "0");
        globalDefaultNamespace.define(intTypeSymbol);

        parentTypes = new HashSet<>();
        parentTypes.add(intTypeSymbol);
        parentTypes.add(boolNullableTypeSymbol);
        boolTypeSymbol = symbolFactory.createScalarTypeSymbol("bool", TypeBool, parentTypes, false, TypeBool, "false");
        globalDefaultNamespace.define(boolTypeSymbol);

        arrayTypeSymbol = symbolFactory.createArrayTypeSymbol("array", TypeArray, stringTypeSymbol, objectTypeSymbol);
        globalDefaultNamespace.define(arrayTypeSymbol);

        resourceTypeSymbol = symbolFactory.createPseudoTypeSymbol("resource");
        globalDefaultNamespace.define(resourceTypeSymbol);

        //predefiend classes
        exceptionTypeSymbol = createClass("Exception");
        //TODO TSPHP-401 Define predefined Exceptions 
        globalDefaultNamespace.define(exceptionTypeSymbol);

        IClassTypeSymbol errorException = createClass("ErrorException");
        errorException.setParent(exceptionTypeSymbol);
        errorException.addParentTypeSymbol(exceptionTypeSymbol);
        globalDefaultNamespace.define(errorException);
    }

    private IClassTypeSymbol createClass(String className) {
        ITSPHPAst classModifier = astHelper.createAst(CLASS_MODIFIER, "cMod");
        ITSPHPAst identifier = astHelper.createAst(TYPE_NAME, className);
        return symbolFactory.createClassTypeSymbol(classModifier, identifier, globalDefaultNamespace);
    }

    private void initMaps() {
        int[] unaryOperatoTypes = new int[]{
            PRE_INCREMENT, PRE_DECREMENT,
            At, BitwiseNot, LogicNot, UNARY_MINUS, UNARY_PLUS,
            POST_INCREMENT, POST_DECREMENT
        };
        for (int i = 0; i < unaryOperatoTypes.length; ++i) {
            unaryOperators.put(unaryOperatoTypes[i], new ArrayList<IMethodSymbol>());
        }

        int[] binaryOperatorTypes = new int[]{
            LogicOrWeak, LogicXorWeak, LogicAndWeak,
            Assign, PlusAssign, MinusAssign, MultiplyAssign, DivideAssign,
            BitwiseAndAssign, BitwiseOrAssign, BitwiseXorAssign,
            ModuloAssign, DotAssign, ShiftLeftAssign, ShiftRightAssign, CASTING_ASSIGN,
            LogicOr, LogicAnd,
            BitwiseOr, BitwiseAnd, BitwiseXor,
            Equal, Identical, NotEqual, NotIdentical, NotEqualAlternative,
            LessThan, LessEqualThan, GreaterThan, GreaterEqualThan,
            ShiftLeft, ShiftRight,
            Plus, Minus, Multiply, Divide, Modulo, Dot,
            CASTING
        };
        for (int i = 0; i < binaryOperatorTypes.length; ++i) {
            binaryOperators.put(binaryOperatorTypes[i], new ArrayList<IMethodSymbol>());
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
        addToBinaryOperators(Dot, methodSymbol);

    }

    private IMethodSymbol createInBuiltMethodSymbol(String methodName) {
        ITSPHPAst methodModifier = astHelper.createAst(METHOD_MODIFIER, "mMod");
        methodModifier.addChild(astHelper.createAst(Public, "public"));
        ITSPHPAst identifier = astHelper.createAst(Identifier, methodName);
        ITSPHPAst returnTypeModifier = astHelper.createAst(TYPE_MODIFIER, "rtMod");
        return symbolFactory.createMethodSymbol(methodModifier, returnTypeModifier, identifier, globalDefaultNamespace);

    }

    private IVariableSymbol createParameter(String parameterName, ITypeSymbol type) {
        return createParameter(parameterName, type, null);
    }

    private IVariableSymbol createParameter(String parameterName, ITypeSymbol type,
            Map<Integer, String> typeModifiers) {
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

    private void defineLogicOperators() {
        Object[][] operators = new Object[][]{
            {"or", LogicOrWeak},
            {"xor", LogicXorWeak},
            {"and", LogicAndWeak},
            {"&&", LogicAnd},
            {"||", LogicOr}
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
        addToUnaryOperators(LogicNot, methodSymbol);
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
            addIntOperator(operator);
        }
        IMethodSymbol methodSymbol = createInBuiltMethodSymbol("~");
        methodSymbol.addParameter(createParameter("expr", intTypeSymbol));
        methodSymbol.setType(intTypeSymbol);
        addToUnaryOperators(BitwiseNot, methodSymbol);

        methodSymbol = createInBuiltMethodSymbol("~");
        methodSymbol.addParameter(createParameter("expr", intNullableTypeSymbol));
        methodSymbol.setType(intTypeSymbol);
        addToUnaryOperators(BitwiseNot, methodSymbol);
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
            {"<", LessThan},
            {"<=", LessEqualThan},
            {">", GreaterThan},
            {">=", GreaterEqualThan}
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
            {"+", Plus},
            {"-", Minus},
            {"*", Multiply},
            {"/", Divide},
            {"%", Modulo}
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
            {"++", PRE_INCREMENT},
            {"++", POST_INCREMENT},
            {"--", PRE_DECREMENT},
            {"--", POST_DECREMENT},
            {"-", UNARY_MINUS},
            {"+", UNARY_PLUS}
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
        addToBinaryOperators(Plus, methodSymbol);

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
