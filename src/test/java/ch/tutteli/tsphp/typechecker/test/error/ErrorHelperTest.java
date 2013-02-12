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

import ch.tutteli.tsphp.common.TSPHPAst;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.typechecker.error.ErrorHelper;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tutteli.tsphp.typechecker.error.IErrorHelper;
import ch.tutteli.tsphp.typechecker.symbols.VariableSymbol;
import ch.tutteli.tsphp.typechecker.test.testutils.ATypeCheckerTest;
import java.util.ArrayList;
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
public class ErrorHelperTest extends ATypeCheckerTest
{

    private String identifier;
    private int lineExisting;
    private int positionExisting;
    private int lineNew;
    private int positionNew;
    String failed;

    public ErrorHelperTest(String theIdentifier, int theLineExisting, int thePositionExisting,
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
    public void testAddAlreadyDefinedExceptionAndRecoverAst1First() {
        TSPHPAst ast1 = createAst(identifier, lineExisting, positionExisting);
        TSPHPAst ast2 = createAst(identifier, lineNew, positionNew);

        TSPHPAst result = ErrorHelperRegistry.get().addAlreadyDefinedExceptionAndRecover(ast1, ast2);
        Assert.assertEquals(failed, ast1, result);
        check(ast1, ast2, ast1, ast2);
    }

    @Test
    public void testAddAlreadyDefinedExceptionAndRecoverAst2First() {
        TSPHPAst ast1 = createAst(identifier, lineNew, positionNew);
        TSPHPAst ast2 = createAst(identifier, lineExisting, positionExisting);

        TSPHPAst result = ErrorHelperRegistry.get().addAlreadyDefinedExceptionAndRecover(ast1, ast2);
        Assert.assertEquals(failed, ast2, result);
        check(ast1, ast2, ast2, ast1);
    }

    @Test
    public void testAddAlreadyDefinedException() {
        TSPHPAst ast1 = createAst(identifier, lineExisting, positionExisting);
        TSPHPAst ast2 = createAst(identifier, lineNew, positionNew);
        ast1.symbol = new VariableSymbol(ast1, new HashSet<Integer>(), "$a");
        ast2.symbol = new VariableSymbol(ast2, new HashSet<Integer>(), "$a");
        ErrorHelperRegistry.get().addAlreadyDefinedException(ast1.symbol, ast2.symbol);
        check(ast1, ast2, ast1, ast2);
    }

    @Test
    public void testAddAlreadyDefinedExceptionList2() {
        TSPHPAst ast1 = createAst(identifier, lineNew, positionNew);
        TSPHPAst ast2 = createAst(identifier, lineExisting, positionExisting);
        List<TSPHPAst> definitionAsts = new ArrayList<>();
        definitionAsts.add(ast1);
        definitionAsts.add(ast2);
        ErrorHelperRegistry.get().addAlreadyDefinedException(definitionAsts);
        check(ast1, ast2, ast1, ast2);
    }

    @Test
    public void testAddAlreadyDefinedExceptionList3() {
        TSPHPAst ast1 = createAst(identifier, lineNew, positionNew);
        TSPHPAst ast2 = createAst(identifier, lineExisting, positionExisting);
        TSPHPAst ast3 = createAst(identifier, lineExisting + 1, positionExisting + 1);
        List<TSPHPAst> definitionAsts = new ArrayList<>();
        definitionAsts.add(ast1);
        definitionAsts.add(ast2);
        definitionAsts.add(ast3);
        ErrorHelperRegistry.get().addAlreadyDefinedException(definitionAsts);
        check(ast1, ast2, new TSPHPAst[][]{
                    {ast1, ast2},
                    {ast1, ast3},});
    }

    protected void check(TSPHPAst ast1, TSPHPAst ast2, TSPHPAst existingDefinition, TSPHPAst newDefinition) {
        check(ast1, ast2, new TSPHPAst[][]{{existingDefinition, newDefinition}});
    }

    protected void check(TSPHPAst ast1, TSPHPAst ast2,
            TSPHPAst[][] expectedExceptions) {
        IErrorHelper errorHelper = ErrorHelperRegistry.get();
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

    private TSPHPAst createAst(String tokenText, int line, int position) {
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
