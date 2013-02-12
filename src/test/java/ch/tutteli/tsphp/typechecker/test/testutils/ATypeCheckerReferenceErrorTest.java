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

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import java.util.List;
import junit.framework.Assert;
import org.antlr.runtime.Token;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ATypeCheckerReferenceErrorTest extends ATypeCheckerReferenceTest
{

    protected DefinitionErrorStruct linesAndPositions;

    public ATypeCheckerReferenceErrorTest(String testString, DefinitionErrorStruct expectedLinesAndPositions) {
        super(testString);
        linesAndPositions = expectedLinesAndPositions;
    }

    
    @Override
    public void verifyReferences(){
        IErrorReporter errorReporter = ErrorHelperRegistry.get();
        Assert.assertTrue(testString + " failed. No exception occured.", errorReporter.hasFoundError());

        List<Exception> exceptions = errorReporter.getExceptions();
        if (exceptions.size() > 1) {
            Assert.fail(testString + " failed. More than one exception occured. "
                    + exceptions.get(0).getMessage() + "--" + exceptions.get(1).getMessage());
        }

        DefinitionException defException = (DefinitionException) exceptions.get(0);
        Token existingToken = defException.getExistingDefinition().getToken();
        Token newToken = defException.getNewDefinition().getToken();
        Assert.assertEquals(testString + " failed. wrong existing line. ",
                linesAndPositions.existingLine, existingToken.getLine());
        Assert.assertEquals(testString + " failed. wrong existing position. ",
                linesAndPositions.existingPosition, existingToken.getCharPositionInLine());

        Assert.assertEquals(testString + " failed. wrong new line. ",
                linesAndPositions.newLine, newToken.getLine());
        Assert.assertEquals(testString + " failed. wrong new position. ",
                linesAndPositions.newPosition, newToken.getCharPositionInLine());

    }
}
