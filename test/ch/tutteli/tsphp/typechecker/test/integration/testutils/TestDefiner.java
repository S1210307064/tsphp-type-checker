package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.typechecker.Definer;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestDefiner extends Definer implements IDefiner, ICreateSymbolListener
{

    private List<Map.Entry<ISymbol, ITSPHPAst>> symbols = new ArrayList<>();
    private ISymbol newlyCreatedSymbol;

    public TestDefiner(TestSymbolFactory aSymbolFactory, IScopeFactory aScopeFactory) {
        super(aSymbolFactory, aScopeFactory);
        aSymbolFactory.registerListener(this);
    }

    public List<Map.Entry<ISymbol, ITSPHPAst>> getSymbols() {
        return symbols;
    }

    @Override
    public void defineUse(INamespaceScope currentScope, ITSPHPAst type, ITSPHPAst alias) {
        super.defineUse(currentScope, type, alias);
        symbols.add(new HashMap.SimpleEntry<>(alias.getSymbol(), type));
    }

    @Override
    public IInterfaceTypeSymbol defineInterface(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds) {
        IInterfaceTypeSymbol symbol = super.defineInterface(currentScope, modifier, identifier, extendsIds);
        ITSPHPAst identifiers = null;
        if (extendsIds.getChildCount() > 0) {
            identifiers = new TSPHPAst();
            appendChildrenFromTo(extendsIds, identifiers);
        }
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, identifiers));
        return symbol;
    }

    @Override
    public IClassTypeSymbol defineClass(IScope currentScope, ITSPHPAst modifier, ITSPHPAst identifier,
            ITSPHPAst extendsIds, ITSPHPAst implementsIds) {
        IClassTypeSymbol scope = super.defineClass(currentScope, modifier, identifier, extendsIds, implementsIds);

        ITSPHPAst identifiers = null;
        if (extendsIds.getChildCount() > 0 || implementsIds.getChildCount() > 0) {
            identifiers = new TSPHPAst();
            appendChildrenFromTo(extendsIds, identifiers);
            appendChildrenFromTo(implementsIds, identifiers);
        }
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, identifiers));

        return scope;
    }

    @Override
    public IMethodSymbol defineMethod(IScope currentScope, ITSPHPAst methodModifier,
            ITSPHPAst returnTypeModifier, ITSPHPAst returnType, ITSPHPAst identifier) {
        IMethodSymbol scope = super.defineMethod(currentScope, methodModifier, returnTypeModifier, returnType, identifier);
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, returnType));
        return scope;
    }

    @Override
    public void defineVariable(IScope currentScope, ITSPHPAst modifier, ITSPHPAst type, ITSPHPAst variableId) {
        super.defineVariable(currentScope, modifier, type, variableId);
        symbols.add(new HashMap.SimpleEntry<>(newlyCreatedSymbol, type));
    }

    @Override
    public void setNewlyCreatedSymbol(ISymbol symbol) {
        newlyCreatedSymbol = symbol;
    }

    private void appendChildrenFromTo(ITSPHPAst source, ITSPHPAst target) {
        int lenght = source.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            target.addChild(AstHelperRegistry.get().copyAst(source.getChild(i)));
        }
    }
}
