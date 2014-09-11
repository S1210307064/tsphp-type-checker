/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols.erroneous;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ASymbol;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ISymbolWithModifier;
import ch.tsphp.common.symbols.modifiers.IModifierSet;

import java.util.List;
import java.util.Map;

public abstract class AErroneousScopedSymbol extends ASymbol implements IErroneousSymbol, IScope, ISymbolWithModifier
{

    public static final String ERROR_MESSAGE_SCOPE = "AErroneousScopedSymbol is not a real scope.";
    public static final String ERROR_MESSAGE_MODIFIER = "AErroneousScopedSymbol is not a real symbol with modifier.";
    private final TSPHPException exception;

    public AErroneousScopedSymbol(ITSPHPAst ast, TSPHPException theException) {
        super(ast, ast.getText());
        exception = theException;
    }

    @Override
    public TSPHPException getException() {
        return exception;
    }

    @Override
    public String getScopeName() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public IScope getEnclosingScope() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public void define(ISymbol symbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public ISymbol resolve(ITSPHPAst ast) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public Map<String, List<ISymbol>> getSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_SCOPE);
    }

    @Override
    public void addModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public IModifierSet getModifiers() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public void setModifiers(IModifierSet modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public void addToInitialisedSymbols(ISymbol symbol, boolean isFullyInitialised) {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public Map<String, Boolean> getInitialisedSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE_MODIFIER);
    }

    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        return true;
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        return true;
    }


}
