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
package ch.tutteli.tsphp.typechecker.test.testutils.typecheck;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceTest;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class ATypeCheckTest extends AReferenceTest
{

    protected TSPHPTypeCheckWalker typeCheckWalker;

    protected abstract void verifyTypeCheck();

    public ATypeCheckTest(String testString) {
        super(testString);
    }

    @Override
    protected void verifyReferences() {
        commonTreeNodeStream.reset();
        typeCheckWalker = new TSPHPTypeCheckWalker(commonTreeNodeStream, controller);
        typeCheckWalker.downup(ast);
        checkErrors();
    }

    protected void checkErrors() {
        IErrorReporter errorHelper = ErrorReporterRegistry.get();
        junit.framework.Assert.assertFalse(testString + " failed. Exceptions occured." + errorHelper.getExceptions(),
                errorHelper.hasFoundError());

        verifyTypeCheck();
    }
}
