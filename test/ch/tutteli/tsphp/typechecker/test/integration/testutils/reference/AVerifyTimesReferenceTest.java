package ch.tutteli.tsphp.typechecker.test.integration.testutils.reference;

import ch.tutteli.tsphp.typechecker.IReferencePhaseController;
import ch.tutteli.tsphp.typechecker.ISymbolResolver;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
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
    protected IReferencePhaseController createReferencePhaseController(
            ISymbolFactory theSymbolFactory,
            ISymbolResolver theSymbolResolver,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {
        return spy(super.createReferencePhaseController(
                theSymbolFactory, theSymbolResolver, theGlobalDefaultNamespace));
    }
}
