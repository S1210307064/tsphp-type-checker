package ch.tutteli.tsphp.typechecker.error;

public final class ErrorReporterRegistry
{

    private static ITypeCheckErrorReporter errorHelper;

    private ErrorReporterRegistry() {
    }

    public static ITypeCheckErrorReporter get() {
        return errorHelper;
    }

    public static void set(ITypeCheckErrorReporter newErrorHelper) {
        errorHelper = newErrorHelper;
    }
}
