package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

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
