/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.utils;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ATypeCheckerDefinitionTest extends ATypeCheckerTest
{

    protected String expectedResult;

    public ATypeCheckerDefinitionTest(String testString, String theExpectedResult) {
        super(testString);
        expectedResult = theExpectedResult;
    }

    @Override
    protected void verify() {
        Assert.assertEquals(testString + " failed.", expectedResult, getSymbolsAsString());
    }

    public String getSymbolsAsString() {
        List<Map.Entry<ISymbol, TSPHPAst>> symbols = testSymbolTable.getSymbols();
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirstSymbol = true;
        for (Map.Entry<ISymbol, TSPHPAst> entry : symbols) {
            if (!isFirstSymbol) {
                stringBuilder.append(" ");
            }
            isFirstSymbol = false;
            stringBuilder.append(getTypesAsString(entry.getValue()))
                    .append(ScopeHelper.getEnclosingScopeNames(entry.getKey().getDefinitionScope()))
                    .append(entry.getKey().toString());
        }
        return stringBuilder.toString();
    }

    private String getTypesAsString(TSPHPAst types) {
        String typesAsString;

        if (types == null) {
            typesAsString = "";
        } else if (types.getChildCount() == 0) {
            typesAsString = getSingleTypeAsString(types);
        } else {
            typesAsString = getMultipleTypesAsString(types);
        }

        return typesAsString;
    }

    private String getSingleTypeAsString(TSPHPAst type) {
        return ScopeHelper.getEnclosingScopeNames(type.scope) + type.getText() + " ";
    }

    private String getMultipleTypesAsString(TSPHPAst types) {

        StringBuilder stringBuilder = new StringBuilder();
        int lenght = types.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            stringBuilder.append(getSingleTypeAsString((TSPHPAst) types.getChild(i)));
        }
        return stringBuilder.toString();
    }
}
