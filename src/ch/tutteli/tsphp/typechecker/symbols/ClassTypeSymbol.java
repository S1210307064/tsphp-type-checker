package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.Set;

public class ClassTypeSymbol extends AScopedTypeSymbol implements IClassTypeSymbol
{

    private IMethodSymbol construct;
    private IVariableSymbol $this;
    private IClassTypeSymbol parent;

    public ClassTypeSymbol(ITSPHPAst definitionAst, Set<Integer> modifiers, String name, IScope enclosingScope,
            ITypeSymbol parentTypeSymbol) {
        super(definitionAst, modifiers, name, enclosingScope, parentTypeSymbol);
    }

    @Override
    public void setConstruct(IMethodSymbol newConstruct) {
        construct = newConstruct;
    }

    @Override
    public IMethodSymbol getConstruct() {
        return construct;
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
    public IClassTypeSymbol getParent() {
        return parent;
    }

    @Override
    public void setParent(IClassTypeSymbol theParent) {
        parent = theParent;
    }

    @Override
    public boolean isFinal() {
        return modifiers.contains(TSPHPDefinitionWalker.Final);
    }

    @Override
    public boolean isAbstract() {
        return modifiers.contains(TSPHPDefinitionWalker.Abstract);
    }
}
