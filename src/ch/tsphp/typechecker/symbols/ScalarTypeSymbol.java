package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;

import java.util.Set;

/**
 * Adopted from the book "Language Implementation Patterns" by Terence Parr.
 */
public class ScalarTypeSymbol extends ATypeSymbol implements IScalarTypeSymbol
{

    private int tokenTypeForCasting;
    private int defaultValueTokenType;
    private boolean isNullable;
    private String defaultValue;

    public ScalarTypeSymbol(
            String name,
            Set<ITypeSymbol> parentTypeSymbol,
            int theTokenTypeForCasting,
            boolean isItNullable,
            int theDefaultValueTokenType,
            String theDefaultValue) {

        super(null, name, parentTypeSymbol);
        init(theTokenTypeForCasting, isItNullable, theDefaultValueTokenType, theDefaultValue);
    }

    public ScalarTypeSymbol(
            String name,
            ITypeSymbol parentTypeSymbol,
            int theTokenTypeForCasting,
            boolean isItNullable,
            int theDefaultValueTokenType,
            String theDefaultValue) {

        super(null, name, parentTypeSymbol);
        init(theTokenTypeForCasting, isItNullable, theDefaultValueTokenType, theDefaultValue);
    }

    private void init(int theTokenTypeForCasting, boolean isItNullable,
            int theDefaultValueTokenType, String theDefaultValue) {
        tokenTypeForCasting = theTokenTypeForCasting;
        isNullable = isItNullable;
        defaultValueTokenType = theDefaultValueTokenType;
        defaultValue = theDefaultValue;
    }

    @Override
    public int getTokenTypeForCasting() {
        return tokenTypeForCasting;
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(defaultValueTokenType, defaultValue);
    }
}