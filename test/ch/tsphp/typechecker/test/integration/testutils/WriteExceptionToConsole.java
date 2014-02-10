package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.exceptions.TSPHPException;

public class WriteExceptionToConsole implements IErrorLogger
{
    @Override
    public void log(TSPHPException exception) {
        System.out.println(exception.getMessage());
    }
}
