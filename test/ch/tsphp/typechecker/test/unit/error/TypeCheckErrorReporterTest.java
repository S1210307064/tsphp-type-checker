/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.error;


import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.DefinitionException;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.typechecker.error.IErrorMessageProvider;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.error.TypeCheckerErrorReporter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
public class TypeCheckErrorReporterTest
{
    private final List<Exception> exceptions = new ArrayList<>();
    private ITypeCheckerErrorReporter errorReporter;

    @Before
    public void setUp() {
        errorReporter = new TypeCheckerErrorReporter(mock(IErrorMessageProvider.class));
        errorReporter.registerErrorLogger(new IErrorLogger()
        {
            @Override
            public void log(TSPHPException ex) {
                exceptions.add(ex);
            }
        });
    }

    @Test
    public void determineAlreadyDefined_SameStartIndexPass1First_Report2First() {
        ITSPHPAst ast1 = createMockAndSpecifyIsDefinedEarlier(false);
        //doesn't really matter what the second is, the comparison should be made with the first passed in
        ITSPHPAst ast2 = createMockAndSpecifyIsDefinedEarlier(false);

        errorReporter.determineAlreadyDefined(ast1, ast2);

        assertReportedInFollowingOrder(ast2, ast1);
        verifyFirstWasCalledAndSecondPassed(ast1, ast2);
    }

    private void verifyFirstWasCalledAndSecondPassed(ITSPHPAst astCalledWasMade, ITSPHPAst astWasUsedAsArg) {
        verify(astCalledWasMade).isDefinedEarlierThan(astWasUsedAsArg);
        verify(astWasUsedAsArg, never()).isDefinedEarlierThan(Matchers.any(ITSPHPAst.class));
    }

    @Test
    public void determineAlreadyDefined_SameStartIndexPass2First_Report1First() {
        //doesn't really matter what the first is, the comparison should be made with the first passed in
        ITSPHPAst ast1 = createMockAndSpecifyIsDefinedEarlier(false);
        ITSPHPAst ast2 = createMockAndSpecifyIsDefinedEarlier(false);

        errorReporter.determineAlreadyDefined(ast2, ast1);

        assertReportedInFollowingOrder(ast1, ast2);
        verifyFirstWasCalledAndSecondPassed(ast2, ast1);
    }

    @Test
    public void determineAlreadyDefined_1EarlierPass1First_Report1AtFirst() {
        ITSPHPAst ast1 = createMockAndSpecifyIsDefinedEarlier(true);
        //doesn't really matter what the second is, the comparison should be made with the first passed in
        ITSPHPAst ast2 = mock(ITSPHPAst.class);

        errorReporter.determineAlreadyDefined(ast1, ast2);

        assertReportedInFollowingOrder(ast1, ast2);
        verifyFirstWasCalledAndSecondPassed(ast1, ast2);
    }

    @Test
    public void determineAlreadyDefined_1EarlierPass2First_Report1AtFirst() {
        ITSPHPAst ast1 = createMockAndSpecifyIsDefinedEarlier(true);
        //doesn't really matter what the second is, the comparison should be made with the first passed in
        ITSPHPAst ast2 = mock(ITSPHPAst.class);

        errorReporter.determineAlreadyDefined(ast2, ast1);

        assertReportedInFollowingOrder(ast1, ast2);
        verifyFirstWasCalledAndSecondPassed(ast2, ast1);
    }

    private ITSPHPAst createMockAndSpecifyIsDefinedEarlier(boolean isDefinedEarlier) {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.isDefinedEarlierThan(Matchers.any(ITSPHPAst.class))).thenReturn(isDefinedEarlier);
        return ast;
    }

    private void assertReportedInFollowingOrder(ITSPHPAst existingDefinition, ITSPHPAst newDefinition) {
        assertTrue("exceptions was empty.", errorReporter.hasFoundError());
        assertThat("more than 1 exception occurred.", exceptions.size(), is(1));

        Exception exception = exceptions.get(0);
        assertTrue("exception was not a DefinitionException.", exception instanceof DefinitionException);
        DefinitionException definitionException = (DefinitionException) exception;
        assertThat(definitionException.getExistingDefinition(), is(existingDefinition));
        assertThat(definitionException.getNewDefinition(), is(newDefinition));
    }


}

