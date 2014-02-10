package ch.tsphp.typechecker;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.scopes.INamespaceScope;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;
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
        token.setType(TSPHPDefinitionWalker.METHOD_CALL_STATIC);
        token.setText("smCall");
        ITSPHPAst typeName = astHelper.createAst(TSPHPDefinitionWalker.TYPE_NAME, getAbsoluteClassName(methodSymbol));
        cast.addChild(typeName);
        ITSPHPAst identifier = astHelper.createAst(TSPHPDefinitionWalker.Identifier, methodSymbol.getName() + "()");
        cast.addChild(identifier);
        ITSPHPAst actualParameters = astHelper.createAst(TSPHPDefinitionWalker.ACTUAL_PARAMETERS, "args");
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
