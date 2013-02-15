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
package ch.tutteli.tsphp.typechecker.test.testutils;

import ch.tutteli.tsphp.common.IParser;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.TSPHPAstAdaptorRegistry;
import ch.tutteli.tsphp.parser.ParserFacade;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckerDefinition;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ATypeCheckerDefinitionTest extends ATypeCheckerTest
{

    protected String testString;
    protected TestSymbolTable symbolTable;
    protected TestScopeFactory scopeFactory;
    protected ITSPHPAst ast;
    public static CommonTreeNodeStream commonTreeNodeStream;

    protected abstract void verifyDefinitions();

    public ATypeCheckerDefinitionTest(String theTestString) {
        super();
        testString = theTestString;
        init();
    }

    private void init() {
        scopeFactory = new TestScopeFactory();
        symbolTable = new TestSymbolTable(new TestSymbolFactory(), scopeFactory);
    }

    public void check() throws RecognitionException {
        IParser parser = new ParserFacade();
        ast = parser.parse(testString);

        Assert.assertFalse(testString + " failed - parser throw exception", parser.hasFoundError());

        commonTreeNodeStream = new CommonTreeNodeStream(TSPHPAstAdaptorRegistry.get(), ast);
        commonTreeNodeStream.setTokenStream(parser.getTokenStream());
        commonTreeNodeStream.reset();
        TSPHPTypeCheckerDefinition definition = new TSPHPTypeCheckerDefinition(commonTreeNodeStream, symbolTable);
        definition.downup(ast);
        
        verifyDefinitions();
    }
}
