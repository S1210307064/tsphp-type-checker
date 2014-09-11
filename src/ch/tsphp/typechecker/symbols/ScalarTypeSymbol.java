/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;

import java.util.Set;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public class ScalarTypeSymbol extends ATypeSymbol implements IScalarTypeSymbol
{

    private int tokenTypeForCasting;
    private int defaultValueTokenType;

    private String defaultValue;

    @SuppressWarnings("checkstyle:parameternumber")
    public ScalarTypeSymbol(
            String name,
            Set<ITypeSymbol> parentTypeSymbol,
            int theTokenTypeForCasting,
            int theDefaultValueTokenType,
            String theDefaultValue) {

        super(null, name, parentTypeSymbol);
        init(theTokenTypeForCasting, theDefaultValueTokenType, theDefaultValue);
    }

    private void init(int theTokenTypeForCasting,
            int theDefaultValueTokenType, String theDefaultValue) {
        tokenTypeForCasting = theTokenTypeForCasting;
        defaultValueTokenType = theDefaultValueTokenType;
        defaultValue = theDefaultValue;
    }

    @Override
    public int getTokenTypeForCasting() {
        return tokenTypeForCasting;
    }

    @Override
    public int getDefaultValueTokenType() {
        return defaultValueTokenType;
    }

    @Override
    public String getDefaultValueAsString() {
        return defaultValue;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(defaultValueTokenType, defaultValue);
    }
}