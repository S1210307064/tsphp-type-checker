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
package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AReferenceErrorTest extends AReferenceTest
{

    protected ReferenceErrorDto[] errorDtos;

    public AReferenceErrorTest(String testString, ReferenceErrorDto[] theErrorDtos) {
        super(testString);
        errorDtos = theErrorDtos;
    }

    @Override
    protected void checkReferences() {
        verifyReferences();
    }

    @Override
    public void verifyReferences() {
        verifyReferences(errorMessagePrefix, exceptions, errorDtos);
    }

    public static void verifyReferences(String errorMessagePrefix, List<Exception> exceptions, ReferenceErrorDto[] errorDtos) {
        IErrorReporter errorReporter = ErrorReporterRegistry.get();
        Assert.assertTrue(errorMessagePrefix + " failed. No exception occured.", errorReporter.hasFoundError());

        Assert.assertEquals(errorMessagePrefix + " failed. More or less exceptions occured." + exceptions.toString(),
                errorDtos.length, exceptions.size());

        for (int i = 0; i < errorDtos.length; ++i) {
            ReferenceException exception = (ReferenceException) exceptions.get(i);

            Assert.assertEquals(errorMessagePrefix + " failed. wrong identifier.",
                    errorDtos[i].identifier, exception.getDefinition().getText());

            Assert.assertEquals(errorMessagePrefix + " failed. wrong line.",
                    errorDtos[i].line, exception.getDefinition().getLine());

            Assert.assertEquals(errorMessagePrefix + " failed. wrong position.",
                    errorDtos[i].position, exception.getDefinition().getCharPositionInLine());
        }

    }
}
