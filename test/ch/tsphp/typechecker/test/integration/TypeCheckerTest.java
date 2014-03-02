package ch.tsphp.typechecker.test.integration;

import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeChecker;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.typechecker.TypeChecker;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TypeCheckerTest
{
    private ITypeCheckerErrorReporter typeCheckerErrorReporter;

    private class DummyTypeChecker extends TypeChecker
    {
        protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
            typeCheckerErrorReporter = super.createTypeCheckerErrorReporter();
            return typeCheckerErrorReporter;
        }
    }

    @Test
    public void Reset_Standard_ErrorLoggersAreStillRegisteredToTypeCheckerErrorReporter() {
        IErrorLogger errorLogger = mock(IErrorLogger.class);

        ITypeChecker typeChecker = new DummyTypeChecker();
        typeChecker.registerErrorLogger(errorLogger);
        typeChecker.reset();
        typeCheckerErrorReporter.alreadyDefined(createAst(), createAst());

        verify(errorLogger).log(any(TSPHPException.class));
    }

    private ITSPHPAst createAst() {
        ITSPHPAst ast = mock(ITSPHPAst.class);
        when(ast.getText()).thenReturn("a");
        when(ast.getLine()).thenReturn(1);
        when(ast.getCharPositionInLine()).thenReturn(2);
        return ast;
    }
}
