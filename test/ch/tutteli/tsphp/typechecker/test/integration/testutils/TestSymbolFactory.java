package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.SymbolFactory;
import java.util.ArrayList;
import java.util.List;

public class TestSymbolFactory extends SymbolFactory
{

    List<ICreateSymbolListener> listeners = new ArrayList<>();

    @Override
    public IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier, IScope currentScope) {
        IInterfaceTypeSymbol symbol = super.createInterfaceTypeSymbol(modifier, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IClassTypeSymbol createClassTypeSymbol(ITSPHPAst classModifierAst, ITSPHPAst identifier, IScope currentScope) {
        IClassTypeSymbol symbol = super.createClassTypeSymbol(classModifierAst, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier, ITSPHPAst identifier,
            IScope currentScope) {
        IMethodSymbol symbol = super.createMethodSymbol(methodModifier, returnTypeModifier, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IVariableSymbol createVariableSymbol(ITSPHPAst typeModifierAst, ITSPHPAst variableId) {
        IVariableSymbol symbol = super.createVariableSymbol(typeModifierAst, variableId);
        updateListener(symbol);
        return symbol;
    }

    public void registerListener(ICreateSymbolListener listener) {
        listeners.add(listener);
    }

    private void updateListener(ISymbol symbol) {
        for (ICreateSymbolListener listener : listeners) {
            listener.setNewlyCreatedSymbol(symbol);
        }
    }
}
