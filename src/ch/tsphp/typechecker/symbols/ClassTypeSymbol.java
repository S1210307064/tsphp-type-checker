/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.scopes.IScopeHelper;

import java.util.Set;

public class ClassTypeSymbol extends APolymorphicTypeSymbol implements IClassTypeSymbol
{

    private IMethodSymbol construct;
    private IVariableSymbol $this;
    private IClassTypeSymbol parent;

    public ClassTypeSymbol(
            IScopeHelper scopeHelper,
            ITSPHPAst definitionAst,
            Set<Integer> modifiers,
            String name,
            IScope enclosingScope,
            ITypeSymbol parentTypeSymbol) {
        super(scopeHelper, definitionAst, modifiers, name, enclosingScope, parentTypeSymbol);
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
