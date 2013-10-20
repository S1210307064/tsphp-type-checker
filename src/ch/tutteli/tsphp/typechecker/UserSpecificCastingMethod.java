package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IAstHelper;
import ch.tutteli.tsphp.common.ITSPHPAst;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.ACTUAL_PARAMETERS;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.Identifier;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.METHOD_CALL_STATIC;
import static ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker.TYPE_NAME;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
import org.antlr.runtime.Token;

public class UserSpecificCastingMethod extends ACastingMethod
{

    private final IMethodSymbol methodSymbol;

    public UserSpecificCastingMethod(IAstHelper theAstHelper, ITypeSymbolWithPHPBuiltInCasting theType,
            IMethodSymbol theMethodSymbol) {
        super(theAstHelper, theType);
        methodSymbol = theMethodSymbol;
    }

    @Override
    protected int getTokenType() {
        return ((ITypeSymbolWithPHPBuiltInCasting) typeSymbol).getTokenTypeForCasting();
    }

    @Override
    public ITSPHPAst createCastAst(ITSPHPAst ast) {
        //create the cast based on the given (to take the given position etc.)
        ITSPHPAst cast = astHelper.createAst(ast);

        //^(METHOD_CALL_STATIC TYPE_NAME Identifier ^(ACTUAL_PARAMETERS)) 
        Token token = ast.getToken();
        token.setType(METHOD_CALL_STATIC);
        token.setText("smCall");
        ITSPHPAst typeName = astHelper.createAst(TYPE_NAME, getAbsoluteClassName(methodSymbol));
        cast.addChild(typeName);
        ITSPHPAst identifier = astHelper.createAst(Identifier, methodSymbol.getName() + "()");
        cast.addChild(identifier);
        ITSPHPAst actualParameters = astHelper.createAst(ACTUAL_PARAMETERS, "args");
        actualParameters.addChild(ast);
        cast.addChild(actualParameters);
        return cast;
    }

    private String getAbsoluteClassName(IMethodSymbol castingMethod) {
        IClassTypeSymbol classSymbol = (IClassTypeSymbol) castingMethod.getEnclosingScope();
        INamespaceScope namespaceScope = (INamespaceScope) classSymbol.getEnclosingScope();
        return namespaceScope.getScopeName() + classSymbol.getName();
    }
}
