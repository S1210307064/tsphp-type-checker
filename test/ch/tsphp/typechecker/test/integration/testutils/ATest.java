/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.IParser;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.parser.ParserFacade;
import ch.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.error.TypeCheckerErrorReporter;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.List;

@Ignore
public abstract class ATest implements IErrorLogger
{

    protected List<Exception> exceptions = new ArrayList<>();
    protected IParser parser;
    protected ITypeCheckerErrorReporter typeCheckErrorReporter;

    public ATest() {
        parser = createParser();
        parser.registerErrorLogger(new WriteExceptionToConsole());

        typeCheckErrorReporter = createTypeCheckErrorReporter();
        typeCheckErrorReporter.registerErrorLogger(this);
    }

    public void log(TSPHPException exception) {
        exceptions.add(exception);
    }

    protected IParser createParser() {
        return new ParserFacade();
    }

    protected ITypeCheckerErrorReporter createTypeCheckErrorReporter() {
        return new TypeCheckerErrorReporter(new ErrorMessageProvider());
    }
}
