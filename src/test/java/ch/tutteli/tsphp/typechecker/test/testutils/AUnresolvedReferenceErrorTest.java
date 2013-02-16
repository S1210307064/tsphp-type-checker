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
import ch.tutteli.tsphp.common.exceptions.UnresolvedReferenceException;
import ch.tutteli.tsphp.typechecker.error.ErrorHelperRegistry;
import ch.tutteli.tsphp.typechecker.error.UnresolvedReferenceErrorDto;
import java.util.List;
import junit.framework.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AUnresolvedReferenceErrorTest extends AReferenceTest
{

    protected UnresolvedReferenceErrorDto[] errorDtos;

    public AUnresolvedReferenceErrorTest(String testString, UnresolvedReferenceErrorDto[] theErrorDtos) {
        super(testString);
        errorDtos = theErrorDtos;
    }

    @Override
    protected void checkReferences() {
        verifyReferences();
    }

    @Override
    public void verifyReferences() {
        String test = testString.replaceAll("\n", "");
        IErrorReporter errorReporter = ErrorHelperRegistry.get();
        Assert.assertTrue(testString + " failed. No exception occured.", errorReporter.hasFoundError());

        List<Exception> exceptions = errorReporter.getExceptions();
        Assert.assertEquals(test + " failed. More or less exceptions occured." + exceptions.toString(), errorDtos.length,
                exceptions.size());

        for (int i = 0; i < errorDtos.length; ++i) {
            UnresolvedReferenceException exception = (UnresolvedReferenceException) exceptions.get(i);
            
            Assert.assertEquals(test + " failed. wrong identifier.",
                    errorDtos[i].identifier, exception.getDefinition().getText());

            Assert.assertEquals(test + " failed. wrong new line.",
                    errorDtos[i].line, exception.getDefinition().getLine());
            
            Assert.assertEquals(test + " failed. wrong new position.",
                    errorDtos[i].position, exception.getDefinition().getCharPositionInLine());
        }
    }
}
