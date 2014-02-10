package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.SymbolFactory;

import java.util.ArrayList;
import java.util.List;

public class TestSymbolFactory extends SymbolFactory
{

    private List<ICreateSymbolListener> listeners = new ArrayList<>();

    public TestSymbolFactory(IScopeHelper theScopeHelper) {
        super(theScopeHelper);
    }

    @Override
    public IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier,
            IScope currentScope) {
        IInterfaceTypeSymbol symbol = super.createInterfaceTypeSymbol(modifier, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IClassTypeSymbol createClassTypeSymbol(ITSPHPAst classModifierAst, ITSPHPAst identifier,
            IScope currentScope) {
        IClassTypeSymbol symbol = super.createClassTypeSymbol(classModifierAst, identifier, currentScope);
        updateListener(symbol);
        return symbol;
    }

    @Override
    public IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier,
            ITSPHPAst identifier,
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
