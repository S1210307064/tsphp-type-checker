package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;

public class WriteExceptionToConsole implements IErrorLogger
{
    @Override
    public void log(TSPHPException exception) {
        System.out.println(exception.getMessage());
    }
}
