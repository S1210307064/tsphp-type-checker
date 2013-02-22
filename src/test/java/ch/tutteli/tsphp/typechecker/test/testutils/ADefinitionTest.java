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

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.TSPHPAstAdaptor;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ADefinitionTest extends ATest
{

    protected String testString;
    protected String errorMessagePrefix;
    protected TestDefiner definer;
    protected TestSymbolTable symbolTable;
    protected TestScopeFactory scopeFactory;
    protected ITSPHPAst ast;
    protected CommonTreeNodeStream commonTreeNodeStream;
    private ITSPHPAstAdaptor adaptor;
    

    protected abstract void verifyDefinitions();

    public ADefinitionTest(String theTestString) {
        super();
        testString = theTestString;
        errorMessagePrefix = testString.replaceAll("\n", " ") + "\n" + testString;
        init();
    }

    private void init() {
        adaptor = new TSPHPAstAdaptor();
        scopeFactory = new TestScopeFactory();
        symbolTable = new TestSymbolTable(new TestSymbolFactory(), scopeFactory, adaptor);
        definer = (TestDefiner) symbolTable.getDefiner();
    }

    public void check() throws RecognitionException {
        ast = parser.parse(testString);

        Assert.assertFalse(testString.replaceAll("\n", " ") + " failed - parser throw exception", parser.hasFoundError());

        commonTreeNodeStream = new CommonTreeNodeStream(adaptor, ast);
        commonTreeNodeStream.setTokenStream(parser.getTokenStream());

        TSPHPDefinitionWalker definition = new TSPHPDefinitionWalker(commonTreeNodeStream, symbolTable.getDefiner());
        definition.downup(ast);

        verifyDefinitions();
    }
}
