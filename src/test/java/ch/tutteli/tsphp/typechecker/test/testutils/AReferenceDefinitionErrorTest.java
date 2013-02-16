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
import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import java.util.List;
import junit.framework.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AReferenceDefinitionErrorTest extends AReferenceTest
{

    protected DefinitionErrorDto[] errorDtos;

    public AReferenceDefinitionErrorTest(String testString, DefinitionErrorDto[] theErrorDtos) {
        super(testString);
        errorDtos = theErrorDtos;
    }

    @Override
    protected void checkReferences() {
        verifyReferences();
    }

    @Override
    public void verifyReferences() {

        IErrorReporter errorReporter = ErrorHelperRegistry.get();
        Assert.assertTrue(errorMessagePrefix + " failed. No exception occured.", errorReporter.hasFoundError());

        List<Exception> exceptions = errorReporter.getExceptions();
        Assert.assertEquals(errorMessagePrefix + " failed. More or less exceptions occured." + exceptions.toString(),
                errorDtos.length, exceptions.size());

        for (int i = 0; i < errorDtos.length; ++i) {

            DefinitionException exception = (DefinitionException) exceptions.get(i);

            Assert.assertEquals(errorMessagePrefix + " failed. wrong existing identifier.",
                    errorDtos[i].identifier, exception.getExistingDefinition().getText());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong existing line.",
                    errorDtos[i].line, exception.getExistingDefinition().getLine());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong existing position.",
                    errorDtos[i].position, exception.getExistingDefinition().getCharPositionInLine());

               Assert.assertEquals(errorMessagePrefix + " failed. wrong existing identifier.",
                    errorDtos[i].identifierNewDefinition, exception.getNewDefinition().getText());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong new line. ",
                    errorDtos[i].lineNewDefinition, exception.getNewDefinition().getLine());
            Assert.assertEquals(errorMessagePrefix + " failed. wrong new position. ",
                    errorDtos[i].positionNewDefinition, exception.getNewDefinition().getCharPositionInLine());
        }
    }
}
