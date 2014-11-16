/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols.erroneous;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;

import java.util.Set;

public class ErroneousTypeSymbol extends AErroneousScopedSymbol implements IErroneousTypeSymbol
{

    public static final String ERROR_MESSAGE = "ErroneousTypeSymbol is not a real class.";
    private final IMethodSymbol construct;
    private IVariableSymbol $this;

    public ErroneousTypeSymbol(ITSPHPAst ast, TSPHPException exception, IMethodSymbol theConstruct) {
        super(ast, exception);
        construct = theConstruct;
    }

    @Override
    public ISymbol resolveWithFallbackToParent(ITSPHPAst ast) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setConstruct(IMethodSymbol newConstruct) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IMethodSymbol getConstruct() {
        return construct;
    }

    @Override
    public boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IVariableSymbol getThis() {
        return $this;
    }

    @Override
    public void setThis(IVariableSymbol theThis) {
        $this = theThis;
    }

    @Override
    public void addParentTypeSymbol(IPolymorphicTypeSymbol aParent) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Set<ISymbol> getAbstractSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public IClassTypeSymbol getParent() {
        return this;
    }

    @Override
    public void setParent(IClassTypeSymbol theParent) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isFalseable() {
        return true;
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.Null, "null");
    }


}
