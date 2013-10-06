package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import java.util.Set;

public class ErroneousClassTypeSymbol extends AErroneousScopedSymbol implements IErroneousClassTypeSymbol
{

    private final IMethodSymbol construct;
    private IVariableSymbol $this;

    public ErroneousClassTypeSymbol(ITSPHPAst ast, TypeCheckerException exception, IMethodSymbol theConstruct) {
        super(ast, exception);
        construct = theConstruct;
    }

    @Override
    public ISymbol resolveWithFallbackToParent(ITSPHPAst ast) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }

    @Override
    public void setConstruct(IMethodSymbol newConstruct) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }

    @Override
    public IMethodSymbol getConstruct() {
        return construct;
    }

    @Override
    public boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
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
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public IClassTypeSymbol getParent() {
        return this;
    }

    @Override
    public void setParent(IClassTypeSymbol theParent) {
        throw new UnsupportedOperationException("ErroneousClassSymbol is not a real class.");
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
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.Null, "null");
    }
}
