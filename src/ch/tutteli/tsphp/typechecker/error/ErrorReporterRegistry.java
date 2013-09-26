package ch.tutteli.tsphp.typechecker.error;

public final class ErrorReporterRegistry
{

    private static IErrorReporter errorHelper;

    private ErrorReporterRegistry() {
    }

    public static IErrorReporter get() {
        return errorHelper;
    }

    public static void set(IErrorReporter newErrorHelper) {
        errorHelper = newErrorHelper;
    }
}
