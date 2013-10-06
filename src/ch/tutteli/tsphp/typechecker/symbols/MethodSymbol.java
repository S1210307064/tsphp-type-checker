package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MethodSymbol extends AScopedSymbol implements IMethodSymbol
{

    private final List<IVariableSymbol> parameters = new ArrayList<>();
    private final Set<Integer> returnTypeModifier;

    public MethodSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            Set<Integer> methodModifiers,
            Set<Integer> theReturnTypeModifier,
            String name,
            IScope enclosingScope) {
        super(scopeHelper, definitionAst, methodModifiers, name, enclosingScope);
        returnTypeModifier = theReturnTypeModifier;
    }

    @Override
    public void addParameter(IVariableSymbol typeSymbol) {
        parameters.add(typeSymbol);
    }

    @Override
    public List<IVariableSymbol> getParameters() {
        return parameters;
    }

    @Override
    public boolean isStatic() {
        return modifiers.contains(TSPHPDefinitionWalker.Static);
    }

    @Override
    public boolean isFinal() {
        return modifiers.contains(TSPHPDefinitionWalker.Final);
    }

    @Override
    public boolean isAbstract() {
        return modifiers.contains(TSPHPDefinitionWalker.Abstract);
    }

    @Override
    public boolean isAlwaysCasting() {
        return returnTypeModifier.contains(TSPHPDefinitionWalker.Cast);
    }

    @Override
    public boolean isPublic() {
        return modifiers.contains(TSPHPDefinitionWalker.Public);
    }

    @Override
    public boolean isProtected() {
        return modifiers.contains(TSPHPDefinitionWalker.Protected);
    }

    @Override
    public boolean isPrivate() {
        return modifiers.contains(TSPHPDefinitionWalker.Private);
    }

    @Override
    public boolean canBeAccessedFrom(int type) {
        return ModifierHelper.canBeAccessedFrom(modifiers, type);
    }

    @Override
    public String toString() {
        return super.toString() + ModifierHelper.getModifiers(new TreeSet<>(returnTypeModifier));
    }
}
