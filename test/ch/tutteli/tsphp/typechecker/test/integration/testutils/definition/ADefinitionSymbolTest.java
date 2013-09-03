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
package ch.tutteli.tsphp.typechecker.test.integration.testutils.definition;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.ScopeTestHelper;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ADefinitionSymbolTest extends ADefinitionTest
{

    protected String expectedResult;

    public ADefinitionSymbolTest(String testString, String theExpectedResult) {
        super(testString);
        expectedResult = theExpectedResult;
    }

    @Override
    protected void verifyDefinitions() {
        super.verifyDefinitions();
        Assert.assertEquals(testString + " failed.", expectedResult, getSymbolsAsString());
    }
    
    public String getSymbolsAsString() {
        List<Map.Entry<ISymbol, ITSPHPAst>> symbols = definer.getSymbols();
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirstSymbol = true;
        for (Map.Entry<ISymbol, ITSPHPAst> entry : symbols) {
            if (!isFirstSymbol) {
                stringBuilder.append(" ");
            }
            isFirstSymbol = false;
            stringBuilder.append(getTypesAsString(entry.getValue()))
                    .append(ScopeTestHelper.getEnclosingScopeNames(entry.getKey().getDefinitionScope()))
                    .append(entry.getKey().toString());
        }
        return stringBuilder.toString();
    }

    protected String getTypesAsString(ITSPHPAst types) {
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

    protected String getSingleTypeAsString(ITSPHPAst type) {
        return ScopeTestHelper.getEnclosingScopeNames(type.getScope()) + type.getText() + " ";
    }

    protected String getMultipleTypesAsString(ITSPHPAst types) {

        StringBuilder stringBuilder = new StringBuilder();
        int lenght = types.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            stringBuilder.append(getSingleTypeAsString((ITSPHPAst) types.getChild(i)));
        }
        return stringBuilder.toString();
    }
}
