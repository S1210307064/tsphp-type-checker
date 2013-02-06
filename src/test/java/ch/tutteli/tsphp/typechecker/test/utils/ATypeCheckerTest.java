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

import ch.tutteli.tsphp.common.IParser;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.TSPHPAstAdaptorRegistry;
import ch.tutteli.tsphp.parser.ParserFacade;
import ch.tutteli.tsphp.typechecker.TSPHPTypeCheckerDefinition;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.scopes.ScopeFactory;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ATypeCheckerTest
{

    private String testString;
    private String expectedResult;
    private TestDefinitionHelper testDefinitionHelper;

    public ATypeCheckerTest(String theTestString, String theExpectedResult) {
        testString = theTestString;
        expectedResult = theExpectedResult;
    }

    public void check() throws RecognitionException {
        IParser parser = new ParserFacade();
        TSPHPAst ast = parser.parse(testString);

        Assert.assertFalse(testString + " failed - parser throw exception", parser.hasFoundError());

        CommonTreeNodeStream commonTreeNodeStream = new CommonTreeNodeStream(TSPHPAstAdaptorRegistry.get(), ast);
        commonTreeNodeStream.setTokenStream(parser.getTokenStream());

        IScopeFactory scopeFactory = new ScopeFactory();

        TestSymbolFactory symbolFactory = new TestSymbolFactory();
        testDefinitionHelper = new TestDefinitionHelper(symbolFactory);

        TSPHPTypeCheckerDefinition definition = new TSPHPTypeCheckerDefinition(commonTreeNodeStream, scopeFactory, testDefinitionHelper);

        definition.downup(ast);
        Assert.assertEquals(testString + " failed.", expectedResult, getSymbolsAsString());
    }

    public String getSymbolsAsString() {
        List<Map.Entry<ISymbol, TSPHPAst>> symbols = testDefinitionHelper.getSymbols();
        StringBuilder stringBuilder = new StringBuilder();
        boolean isFirstSymbol = true;
        for (Map.Entry<ISymbol, TSPHPAst> entry : symbols) {
            if (!isFirstSymbol) {
                stringBuilder.append(" ");
            }
            isFirstSymbol = false;
            stringBuilder.append(getTypesAsString(entry.getValue()));
            stringBuilder.append(entry.getKey());
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
        return getEnclosingScopeNames(type.scope) + type.getText() + " ";
    }

    private String getEnclosingScopeNames(IScope scope) {
        StringBuilder stringBuilder = new StringBuilder();
        while (scope != null) {
            if (isNotDefaultNamespace(scope)) {
                stringBuilder.insert(0, ".");
                stringBuilder.insert(0, scope.getScopeName());

            }
            scope = scope.getEnclosingScope();
        }
        return stringBuilder.toString();
    }

    private String getMultipleTypesAsString(TSPHPAst types) {

        StringBuilder stringBuilder = new StringBuilder();
        int lenght = types.getChildCount();
        for (int i = 0; i < lenght; ++i) {
            stringBuilder.append(getSingleTypeAsString((TSPHPAst) types.getChild(i)));
        }
        return stringBuilder.toString();
    }

    private boolean isNotDefaultNamespace(IScope scope) {
        return !scope.getScopeName().equals(IScope.DEFAULT_NAMESPACE);
    }
}
