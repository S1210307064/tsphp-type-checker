package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.typechecker.IOverloadResolver;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.ITypeCheckerController;
import ch.tutteli.tsphp.typechecker.ITypeSystem;
import ch.tutteli.tsphp.typechecker.TypeCheckerController;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestDefiner;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TestSymbolFactory;
import org.junit.Ignore;
import org.mockito.exceptions.base.MockitoAssertionError;

import static org.mockito.Mockito.spy;

@Ignore
public abstract class AVerifyTimesReferenceTest extends AReferenceTest
{
    protected int howManyTimes;

    public AVerifyTimesReferenceTest(String testString, int times) {
        super(testString);
        howManyTimes = times;
    }

    protected abstract void verifyTimes();

    @Override
    protected void verifyReferences() {
        try {
            verifyTimes();
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    @Override
    protected ITypeCheckerController createTypeCheckerController(
            TestSymbolFactory theSymbolFactory,
            ITypeSystem theTypeSystem,
            TestDefiner theDefiner,
            ISymbolResolver theSymbolResolver,
            IOverloadResolver theMethodResolver) {
        return spy(new TypeCheckerController(
                theSymbolFactory,
                theTypeSystem,
                theDefiner,
                theSymbolResolver,
                theMethodResolver,
                typeCheckerAstHelper));
    }
}
