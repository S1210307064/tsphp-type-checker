/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.ISymbolResolver;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.symbols.IModifierHelper;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
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
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ITypeSystem theTypeSystem,
            IModifierHelper theModifierHelper,
            IGlobalNamespaceScope theGlobalDefaultNamespace) {
        return spy(super.createReferencePhaseController(
                theSymbolFactory,
                theSymbolResolver,
                theTypeCheckerErrorReporter,
                theTypeSystem,
                theModifierHelper,
                theGlobalDefaultNamespace));
    }
}
