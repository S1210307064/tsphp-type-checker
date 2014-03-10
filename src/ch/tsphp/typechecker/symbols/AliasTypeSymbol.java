/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;

public class AliasTypeSymbol extends ATypeSymbol implements IAliasTypeSymbol
{

    public AliasTypeSymbol(ITSPHPAst definitionAst, String name, ITypeSymbol parentTypeSymbol) {
        super(definitionAst, name, parentTypeSymbol);
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        throw new UnsupportedOperationException("AliasTypeSymbol does not have an default value.");
    }
}
