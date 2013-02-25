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

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.*;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IArrayTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPseudoTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IScalarTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class SymbolTable implements ISymbolTable
{

    private ISymbolFactory symbolFactory;
    private IAstHelper astHelper;
    //
    private Map<Integer, List<IMethodSymbol>> unaryOperators = new HashMap<>();
    private Map<Integer, List<IMethodSymbol>> binaryOperators = new HashMap<>();
    Map<ITypeSymbol, Map<ITypeSymbol, IMethodSymbol>> explicitCastings = new HashMap<>();
    //
    private IScalarTypeSymbol boolTypeSymbol;
    private IScalarTypeSymbol intTypeSymbol;
    private IScalarTypeSymbol floatTypeSymbol;
    private IScalarTypeSymbol stringTypeSymbol;
    private IArrayTypeSymbol arrayTypeSymbol;
    private IPseudoTypeSymbol resourceTypeSymbol;
    private IPseudoTypeSymbol objectTypeSymbol;
    //
    private IGlobalNamespaceScope globalDefaultNamespace;

    public SymbolTable(ISymbolFactory theSymbolFactory, IAstHelper theAstHelper,
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
    public Map<ITypeSymbol, Map<ITypeSymbol, IMethodSymbol>> getExplicitCastings() {
        return explicitCastings;
    }

    @Override
    public IScalarTypeSymbol getBoolTypeSymbol() {
        return boolTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getIntTypeSymbol() {
        return intTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getFloatTypeSymbol() {
        return floatTypeSymbol;
    }

    @Override
    public IScalarTypeSymbol getStringTypeSymbol() {
        return stringTypeSymbol;
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
    public void initTypeSystem() {
        defineBuiltInTypes();
        initMaps();
        defineOperators();
        defineExplicitCastings();
    }

    private void defineBuiltInTypes() {
        globalDefaultNamespace.define(symbolFactory.createPseudoTypeSymbol("void"));

        ITypeSymbol object = symbolFactory.createPseudoTypeSymbol("object");
        symbolFactory.setObjectTypeSymbol(object);
        globalDefaultNamespace.define(object);

        stringTypeSymbol = symbolFactory.createScalarTypeSymbol("string", TypeString, object);
        globalDefaultNamespace.define(stringTypeSymbol);

        floatTypeSymbol = symbolFactory.createScalarTypeSymbol("float", TypeFloat, stringTypeSymbol);
        globalDefaultNamespace.define(floatTypeSymbol);

        intTypeSymbol = symbolFactory.createScalarTypeSymbol("int", TypeInt, floatTypeSymbol);
        globalDefaultNamespace.define(intTypeSymbol);

        boolTypeSymbol = symbolFactory.createScalarTypeSymbol("bool", TypeBool, intTypeSymbol);
        globalDefaultNamespace.define(boolTypeSymbol);

        arrayTypeSymbol = symbolFactory.createArrayTypeSymbol("array", TypeArray, object);
        globalDefaultNamespace.define(arrayTypeSymbol);

        globalDefaultNamespace.define(symbolFactory.createPseudoTypeSymbol("resource"));

        //predefiend classes
        ITSPHPAst classModifier = astHelper.createAst(CLASS_MODIFIER, "cMod");
        ITSPHPAst identifier = astHelper.createAst(TYPE_NAME, "Exception");
        globalDefaultNamespace.define(symbolFactory.createClassTypeSymbol(classModifier, identifier, globalDefaultNamespace));
    }

    private void initMaps() {
        int[] unaryOperatoTypes = new int[]{
            PRE_INCREMENT, PRE_DECREMENT,
            At, BitwiseNot, LogicNot, UNARY_MINUS,
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
        methodSymbol.addParameter(createParameter("left", stringTypeSymbol));
        methodSymbol.addParameter(createParameter("right", stringTypeSymbol));
        methodSymbol.setType(stringTypeSymbol);
        addToBinaryOperators(GreaterThan, methodSymbol);

    }

    @Override
    public IMethodSymbol createPHPInBuiltCastingMethod(ITypeSymbolWithPHPBuiltInCasting typeSymbol) {
        //cast is a reserved keyword and indicates in this particular case, that the default casting mechanism
        //from PHP shall be used. However, the name could also be different, it is not examined at any place.
        IMethodSymbol methodSymbol = createInBuiltMethodSymbol("cast");
        methodSymbol.setType(typeSymbol);
        return methodSymbol;
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

    private IVariableSymbol createParameter(String parameterName, ITypeSymbol type, Map<Integer, String> typeModifiers) {
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
            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", intTypeSymbol));
            methodSymbol.addParameter(createParameter("right", intTypeSymbol));
            methodSymbol.setType(intTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);
        }

        IMethodSymbol methodSymbol = createInBuiltMethodSymbol("~");
        methodSymbol.addParameter(createParameter("expr", intTypeSymbol));
        methodSymbol.setType(intTypeSymbol);
        addToUnaryOperators(BitwiseNot, methodSymbol);
    }

    private void defineRelationalOperators() {
        Object[][] operators = new Object[][]{
            {"<", LessThan},
            {"<=", LessEqualThan},
            {">", GreaterThan},
            {">=", GreaterEqualThan},};
        for (Object[] operator : operators) {
            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", intTypeSymbol));
            methodSymbol.addParameter(createParameter("right", intTypeSymbol));
            methodSymbol.setType(boolTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);

            methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", floatTypeSymbol));
            methodSymbol.addParameter(createParameter("right", floatTypeSymbol));
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
            {"%", Modulo},};

        for (Object[] operator : operators) {
            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", intTypeSymbol));
            methodSymbol.addParameter(createParameter("right", intTypeSymbol));
            methodSymbol.setType(intTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);

            methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("left", floatTypeSymbol));
            methodSymbol.addParameter(createParameter("right", floatTypeSymbol));
            methodSymbol.setType(floatTypeSymbol);
            addToBinaryOperators((int) operator[1], methodSymbol);
        }

        operators = new Object[][]{
            {"++", PRE_INCREMENT},
            {"++", POST_INCREMENT},
            {"--", PRE_DECREMENT},
            {"--", POST_DECREMENT},
            {"-", UNARY_MINUS}
        };
        for (Object[] operator : operators) {
            IMethodSymbol methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("expr", intTypeSymbol));
            methodSymbol.setType(intTypeSymbol);
            addToUnaryOperators((int) operator[1], methodSymbol);

            methodSymbol = createInBuiltMethodSymbol((String) operator[0]);
            methodSymbol.addParameter(createParameter("expr", floatTypeSymbol));
            methodSymbol.setType(floatTypeSymbol);
            addToUnaryOperators((int) operator[1], methodSymbol);
        }
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
        ITypeSymbolWithPHPBuiltInCasting[][] castings = new ITypeSymbolWithPHPBuiltInCasting[][]{
            {arrayTypeSymbol, boolTypeSymbol},
            //TODO check if array cast to int, float and string
            {stringTypeSymbol, floatTypeSymbol},
            {stringTypeSymbol, intTypeSymbol},
            {stringTypeSymbol, boolTypeSymbol},
            {floatTypeSymbol, intTypeSymbol},
            {floatTypeSymbol, boolTypeSymbol},
            {intTypeSymbol, boolTypeSymbol},};
        for (ITypeSymbolWithPHPBuiltInCasting[] fromTo : castings) {
            addToExplicitTypeCastings(fromTo[0], fromTo[1], createPHPInBuiltCastingMethod(fromTo[1]));
        }
    }

    private void addToExplicitTypeCastings(ITypeSymbol from, ITypeSymbol to, IMethodSymbol methodSymbol) {
        if (!explicitCastings.containsKey(from)) {
            explicitCastings.put(from, new HashMap<ITypeSymbol, IMethodSymbol>());
        }
        explicitCastings.get(from).put(to, methodSymbol);
    }
}
