/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.scopes.IScopeHelper;

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

    //Warning! start code duplication - same as in GlobalNamespaceScope
    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && initialisedSymbols.get(symbolName);
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && !initialisedSymbols.get(symbolName);
    }
    //Warning! end code duplication - same as in GlobalNamespaceScope
}
