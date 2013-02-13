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
import ch.tutteli.tsphp.typechecker.error.ErrorHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeFactory;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class RemoteTreeGrammarDebugger
{

    public static void main(String[] args) throws Exception {

        IParser parser = new ParserFacade();
        ITSPHPAst ast = parser.parse("$a=1, $b, $c;");
        CommonTreeNodeStream commonTreeNodeStream = new CommonTreeNodeStream(TSPHPAstAdaptorRegistry.get(), ast);
        commonTreeNodeStream.setTokenStream(parser.getTokenStream());

        TestSymbolFactory testSymbolFactory = new TestSymbolFactory();
        TSPHPTypeCheckerDefinition definition = new TSPHPTypeCheckerDefinition(
                commonTreeNodeStream, new TestSymbolTable(testSymbolFactory, new TestScopeFactory()));

        definition.downup(ast);
        System.exit(0);

    }
}