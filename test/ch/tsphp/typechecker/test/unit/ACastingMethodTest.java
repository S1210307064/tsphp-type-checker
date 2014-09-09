/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit;

import ch.tsphp.common.AstHelper;
import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.TSPHPAstAdaptor;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.ACastingMethod;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ACastingMethodTest
{
    public static int DUMMY_TOKEN_TYPE = -10;
    public static String DUMMY_TYPE_NAME = "dummyType";

    class DummyCastingMethod extends ACastingMethod
    {


        public DummyCastingMethod(IAstHelper theAstHelper, ITypeSymbol theType) {
            super(theAstHelper, theType);
        }

        @Override
        protected int getTokenType() {
            return DUMMY_TOKEN_TYPE;
        }
    }


    @Test
    public void createCastAst_Standard_RootASTIsCast() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol typeSymbol = createDummyTypeSymbol(false, false);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");

        ACastingMethod castingMethod = createCastingMethod(astHelper, typeSymbol);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        assertThat(result.getType(), is(TSPHPDefinitionWalker.CAST));
    }

    @Test
    public void createCastAst_Standard_ReturnCastWithCorrespondingType() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol typeSymbol = createDummyTypeSymbol(false, false);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");

        ACastingMethod castingMethod = createCastingMethod(astHelper, typeSymbol);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        ITSPHPAst typeNameAst = result.getChild(0).getChild(1);
        assertThat(typeNameAst.getType(), is(DUMMY_TOKEN_TYPE));
        assertThat(typeNameAst.getText(), is(DUMMY_TYPE_NAME));
        assertThat(typeNameAst.getEvalType(), is(typeSymbol));
    }

    @Test
    public void createCastAst_Standard_RootASTHasSameLineAndPositionAsPassedAST() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol typeSymbol = createDummyTypeSymbol(false, false);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");
        int line = 123;
        int pos = 3;
        ast.getToken().setLine(line);
        ast.getToken().setCharPositionInLine(pos);

        ACastingMethod castingMethod = createCastingMethod(astHelper, typeSymbol);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        assertThat(result.getToken().getLine(), is(line));
        assertThat(result.getToken().getCharPositionInLine(), is(pos));
    }

    @Test
    public void createCastAst_NoModifiers_ReturnEmptyTMod() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol typeSymbol = createDummyTypeSymbol(false, false);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");

        ACastingMethod castingMethod = createCastingMethod(astHelper, typeSymbol);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        ITSPHPAst tMod = result.getChild(0).getChild(0);
        assertThat(tMod.getType(), is(TSPHPDefinitionWalker.TYPE_MODIFIER));
        assertThat(tMod.getChildren(), is(nullValue()));
    }

    @Test
    public void createCastAst_IsFalseable_ReturnTModWithFalseable() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol type = createDummyTypeSymbol(true, false);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");

        ACastingMethod castingMethod = createCastingMethod(astHelper, type);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        ITSPHPAst tMod = result.getChild(0).getChild(0);
        assertThat(tMod.getType(), is(TSPHPDefinitionWalker.TYPE_MODIFIER));
        assertThat(tMod.getChildren().size(), is(1));
        assertThat(tMod.getChild(0).getType(), is(TSPHPDefinitionWalker.LogicNot));
    }

    @Test
    public void createCastAst_IsNullable_ReturnTModWithNullable() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol type = createDummyTypeSymbol(false, true);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");

        ACastingMethod castingMethod = createCastingMethod(astHelper, type);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        ITSPHPAst tMod = result.getChild(0).getChild(0);
        assertThat(tMod.getType(), is(TSPHPDefinitionWalker.TYPE_MODIFIER));
        assertThat(tMod.getChildren().size(), is(1));
        assertThat(tMod.getChild(0).getType(), is(TSPHPDefinitionWalker.QuestionMark));
    }

    @Test
    public void createCastAst_IsFalseableAndNullable_ReturnTModWithFalseableAndNullable() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol type = createDummyTypeSymbol(true, true);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");

        ACastingMethod castingMethod = createCastingMethod(astHelper, type);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        ITSPHPAst tMod = result.getChild(0).getChild(0);
        assertThat(tMod.getType(), is(TSPHPDefinitionWalker.TYPE_MODIFIER));
        assertThat(tMod.getChildren().size(), is(2));
        assertThat(tMod.getChild(0).getType(), is(TSPHPDefinitionWalker.LogicNot));
        assertThat(tMod.getChild(1).getType(), is(TSPHPDefinitionWalker.QuestionMark));
    }

    @Test
    public void createCastAst_Standard_ExpressionCorrespondsPassedAST() {
        IAstHelper astHelper = new AstHelper(new TSPHPAstAdaptor());
        ITypeSymbol typeSymbol = createDummyTypeSymbol(false, false);
        ITSPHPAst ast = astHelper.createAst(TSPHPDefinitionWalker.VariableId, "$a");

        ACastingMethod castingMethod = createCastingMethod(astHelper, typeSymbol);
        ITSPHPAst result = castingMethod.createCastAst(ast);

        ITSPHPAst expression = result.getChild(1);
        assertThat(expression, is(ast));
    }

    protected ACastingMethod createCastingMethod(IAstHelper astHelper, ITypeSymbol type) {
        return new DummyCastingMethod(astHelper, type);
    }

    protected ITypeSymbol createDummyTypeSymbol(boolean isFalseable, boolean isNullable) {
        ITypeSymbol typeSymbol = mock(ITypeSymbol.class);
        when(typeSymbol.isFalseable()).thenReturn(isFalseable);
        when(typeSymbol.isNullable()).thenReturn(isNullable);
        when(typeSymbol.getName()).thenReturn(DUMMY_TYPE_NAME);
        return typeSymbol;
    }
}
