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
package ch.tutteli.tsphp.typechecker.test.error;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.symbols.VariableSymbol;
import ch.tutteli.tsphp.typechecker.test.testutils.ATest;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@RunWith(Parameterized.class)
public class ErrorReporterTest extends ATest
{

    private String identifier;
    private int lineExisting;
    private int positionExisting;
    private int lineNew;
    private int positionNew;
    String failed;

    public ErrorReporterTest(String theIdentifier, int theLineExisting, int thePositionExisting,
            int theLineNew, int thePositionNew) {
        super();
        identifier = theIdentifier;
        lineExisting = theLineExisting;
        positionExisting = thePositionExisting;
        lineNew = theLineNew;
        positionNew = thePositionNew;

        failed = identifier + " " + lineExisting + ":" + positionExisting
                + " / " + lineNew + ":" + positionNew + " failed";
    }

    @Test
    public void testAddAlreadyDefinedExceptionAndRecover() {
        ITSPHPAst ast1 = createAst(identifier, lineExisting, positionExisting);
        ITSPHPAst ast2 = createAst(identifier, lineNew, positionNew);

        ErrorReporterRegistry.get().alreadyDefined(ast1, ast2);
        check(ast1, ast2, ast1, ast2);
    }

    @Test
    public void testAddAlreadyDefinedException() {
        ITSPHPAst ast1 = createAst(identifier, lineExisting, positionExisting);
        ITSPHPAst ast2 = createAst(identifier, lineNew, positionNew);
        ast1.setSymbol(new VariableSymbol(ast1, new HashSet<Integer>(), "$a"));
        ast2.setSymbol(new VariableSymbol(ast2, new HashSet<Integer>(), "$a"));
        ErrorReporterRegistry.get().alreadyDefined(ast1.getSymbol(), ast2.getSymbol());
        check(ast1, ast2, ast1, ast2);
    }

    protected void check(ITSPHPAst ast1, ITSPHPAst ast2, ITSPHPAst existingDefinition, ITSPHPAst newDefinition) {
        check(ast1, ast2, new ITSPHPAst[][]{{existingDefinition, newDefinition}});
    }

    protected void check(ITSPHPAst ast1, ITSPHPAst ast2,
            ITSPHPAst[][] expectedExceptions) {
        IErrorReporter errorHelper = ErrorReporterRegistry.get();
        Assert.assertTrue(failed + ", exceptions was empty.", errorHelper.hasFoundError());
        List<Exception> exceptions = errorHelper.getExceptions();

        Assert.assertEquals(failed + ", more than 1 exception occured.", expectedExceptions.length, exceptions.size());

        int count = 0;
        for (Exception exception : exceptions) {
            Assert.assertTrue(failed + ", exception was not a DefinitionException.",
                    exception instanceof DefinitionException);

            DefinitionException definitionException = (DefinitionException) exception;

            Assert.assertEquals(expectedExceptions[count][0], definitionException.getExistingDefinition());
            Assert.assertEquals(expectedExceptions[count][1], definitionException.getNewDefinition());
            ++count;
        }
    }

    private ITSPHPAst createAst(String tokenText, int line, int position) {
        Token token = new CommonToken(0, tokenText);
        token.setLine(line);
        token.setCharPositionInLine(position);
        return new TSPHPAst(token);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                    {"$a", 1, 0, 1, 1},
                    {"$a", 1, 0, 2, 0},
                    {"$a", 1, 10, 2, 0}
                });
    }
}
