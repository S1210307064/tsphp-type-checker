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
package ch.tutteli.tsphp.typechecker.test.testutils.definition;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.TSPHPAstAdaptor;
import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ISymbolTable;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.OverloadResolver;
import ch.tutteli.tsphp.typechecker.SymbolResolver;
import ch.tutteli.tsphp.typechecker.SymbolTable;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorReporter;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.testutils.ATest;
import ch.tutteli.tsphp.typechecker.test.testutils.TestDefiner;
import ch.tutteli.tsphp.typechecker.test.testutils.TestScopeFactory;
import ch.tutteli.tsphp.typechecker.test.testutils.TestSymbolFactory;
import ch.tutteli.tsphp.typechecker.utils.AstHelper;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
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
    protected ITypeCheckerController controller;
    protected ISymbolTable symbolTable;
    protected TestScopeFactory scopeFactory;
    protected ITSPHPAst ast;
    protected CommonTreeNodeStream commonTreeNodeStream;
    protected ITSPHPAstAdaptor adaptor;

    protected abstract void verifyDefinitions();

    public ADefinitionTest(String theTestString) {
        super();
        testString = theTestString;
        errorMessagePrefix = testString.replaceAll("\n", " ") + "\n" + testString;
        init();
    }

    private void init() {
        adaptor = new TSPHPAstAdaptor();
        IAstHelper astHelper = new AstHelper(adaptor);
        scopeFactory = new TestScopeFactory();
        TestSymbolFactory symbolFactory = new TestSymbolFactory();
        definer = new TestDefiner(symbolFactory, scopeFactory);
        symbolTable = new SymbolTable(symbolFactory, astHelper, definer.getGlobalDefaultNamespace());
        ISymbolResolver symbolResolver = new SymbolResolver(symbolFactory, definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());
        IOverloadResolver methodResolver = new OverloadResolver(symbolTable);

        controller = new TypeCheckerController(
                symbolFactory,
                symbolTable,
                definer,
                symbolResolver,
                methodResolver,
                astHelper);
    }

    public void check() throws RecognitionException {
        ast = parser.parse(testString);

        Assert.assertFalse(testString.replaceAll("\n", " ") + " failed - parser throw exception", parser.hasFoundError());

        commonTreeNodeStream = new CommonTreeNodeStream(adaptor, ast);
        commonTreeNodeStream.setTokenStream(parser.getTokenStream());

        TSPHPDefinitionWalker definition = new TSPHPDefinitionWalker(commonTreeNodeStream, controller.getDefiner());
        definition.downup(ast);

        verifyDefinitions();
    }
}
