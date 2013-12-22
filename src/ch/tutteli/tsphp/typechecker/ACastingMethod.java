package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.IAstHelper;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import org.antlr.runtime.Token;

public abstract class ACastingMethod implements ICastingMethod
{

    protected final IAstHelper astHelper;
    protected final ITypeSymbol typeSymbol;
    protected ITypeSymbol parentTypeWhichProvidesCast;

    public ACastingMethod(IAstHelper theAstHelper, ITypeSymbol theType) {
        astHelper = theAstHelper;
        typeSymbol = theType;
    }

    protected abstract int getTokenType();

    @Override
    public ITypeSymbol getType() {
        return typeSymbol;
    }

    @Override
    public ITypeSymbol getParentTypeWhichProvidesCast() {
        return parentTypeWhichProvidesCast;
    }

    @Override
    public void setParentTypeWhichProvidesCast(ITypeSymbol parentTypeSymbol) {
        parentTypeWhichProvidesCast = parentTypeSymbol;
    }

    @Override
    public ITSPHPAst createCastAst(ITSPHPAst ast) {
        //create the cast based on the given (to take the given position etc.)
        ITSPHPAst cast = astHelper.createAst(ast);

        //^(CASTING ^(TYPE (TYPE_MODIFIER ?) type) expression)
        Token token = cast.getToken();
        token.setType(TSPHPDefinitionWalker.CAST);
        token.setText("casting");
        ITSPHPAst typeRoot = astHelper.createAst(TSPHPDefinitionWalker.TYPE, "type");
        ITSPHPAst typeModifier = astHelper.createAst(TSPHPDefinitionWalker.TYPE_MODIFIER, "tMod");

        String typeName = typeSymbol.getName();
        if (typeName.endsWith("?")) {
            typeModifier.addChild(astHelper.createAst(TSPHPDefinitionWalker.QuestionMark, "?"));
            typeName = typeName.substring(0, typeName.length() - 1);
        }
        ITSPHPAst type = astHelper.createAst(getTokenType(), typeName);
        type.setEvalType(typeSymbol);
        typeRoot.addChild(typeModifier);
        typeRoot.addChild(type);
        cast.addChild(typeRoot);
        cast.addChild(ast);
        return cast;
    }
}
