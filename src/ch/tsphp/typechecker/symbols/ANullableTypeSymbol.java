/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

import java.util.Set;

public abstract class ANullableTypeSymbol extends ATypeSymbol
{

    public ANullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(null, name, parentTypeSymbol);
    }

    public ANullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbols) {
        super(null, name, parentTypeSymbols);
    }

    @Override
    @SuppressWarnings("PMD.EmptyMethodInAbstractClassShouldBeAbstract")
    public boolean isNullable() {
        return true;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.Null, "null");
    }
}
