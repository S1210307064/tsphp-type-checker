/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.reference;

import org.junit.Ignore;

@Ignore
public abstract class ATypeSystemTest extends AReferenceTest
{
    public ATypeSystemTest() {
        super("");
    }

    @Override
    public void check() {
        throw new UnsupportedOperationException("Extend AReferenceTest if you want to use this method.");
    }

    @Override
    protected void verifyReferences() {
        throw new UnsupportedOperationException("Extend AReferenceTest if you want to use this method.");
    }
}
