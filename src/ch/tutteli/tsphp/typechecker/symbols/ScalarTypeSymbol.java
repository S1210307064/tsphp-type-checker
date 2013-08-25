/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.symbols;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import java.util.Set;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 *
 * Adopted from the book Language Implementation Patterns by Terence Parr
 */
public class ScalarTypeSymbol extends ATypeSymbol implements IScalarTypeSymbol
{

    private int tokenTypeForCasting;
    private int defaultValueTokenType;
    private boolean isNullable;
    private String defaultValue;

    public ScalarTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbol, int tokenTypeForCasting, boolean isItNullable,
            int defaultValueTokenType, String defaultValue) {
        super(null, name, parentTypeSymbol);
        init(tokenTypeForCasting, isItNullable, defaultValueTokenType, defaultValue);
    }

    public ScalarTypeSymbol(String name, ITypeSymbol parentTypeSymbol, int tokenTypeForCasting, boolean isItNullable,
            int defaultValueTokenType, String defaultValue) {
        super(null, name, parentTypeSymbol);
        init(tokenTypeForCasting, isItNullable, defaultValueTokenType, defaultValue);
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