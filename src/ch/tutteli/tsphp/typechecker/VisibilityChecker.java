package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolWithAccessModifier;

public class VisibilityChecker implements IVisibilityChecker
{

    @Override
    public void checkAccess(ISymbolWithAccessModifier methodSymbol, IPolymorphicTypeSymbol polymorphicTypeSymbol,
            IViolationCaller visibilityViolationCaller, ITSPHPAst calleeOrAccessor, ITSPHPAst identifier) {

        int accessedFrom;

        String calleeOrAccessorText = calleeOrAccessor.getText();
        switch (calleeOrAccessorText) {
            case "$this":
            case "self":
                accessedFrom = methodSymbol.getDefinitionScope() == polymorphicTypeSymbol
                        ? TSPHPDefinitionWalker.Private
                        : TSPHPDefinitionWalker.Protected;

                break;
            case "parent":
                accessedFrom = TSPHPDefinitionWalker.Protected;
                break;
            default:
                accessedFrom = TSPHPDefinitionWalker.Public;
                break;
        }

        if (!methodSymbol.canBeAccessedFrom(accessedFrom)) {
            visibilityViolationCaller.callAppropriateMethod(identifier, methodSymbol, accessedFrom);
        }
    }
}