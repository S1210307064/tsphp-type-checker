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
package ch.tutteli.tsphp.typechecker.test.testutils.reference;

import ch.tutteli.tsphp.common.IErrorReporter;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.test.testutils.definition.ADefinitionTest;
import junit.framework.Assert;
import org.junit.Ignore;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@Ignore
public abstract class AReferenceTest extends ADefinitionTest
{

    protected TSPHPReferenceWalker reference;

    public AReferenceTest(String testString) {
        super(testString);
    }

    protected abstract void verifyReferences();

    protected void checkReferences() {
        IErrorReporter errorHelper = ErrorReporterRegistry.get();
        Assert.assertFalse(testString + " failed. Exceptions occured." + exceptions,
                errorHelper.hasFoundError());

        verifyReferences();

    }

    @Override
    protected final void verifyDefinitions() {
        super.verifyDefinitions();
        commonTreeNodeStream.reset();
        reference = new TSPHPReferenceWalker(commonTreeNodeStream, controller);
        reference.downup(ast);
        checkReferences();
    }

    protected static String getAliasFullType(String type) {
        return type.substring(0, 1).equals("\\") ? type : "\\" + type;
    }

    protected static String getFullName(String namespace, String type) {
        String fullType = type;
        if (!type.substring(0, 1).equals("\\")) {
            fullType = namespace + type;
        }
        return fullType;
    }
}
