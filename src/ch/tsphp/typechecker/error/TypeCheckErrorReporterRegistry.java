package ch.tsphp.typechecker.error;

public final class TypeCheckErrorReporterRegistry
{

    private static ITypeCheckErrorReporter errorHelper;

    private TypeCheckErrorReporterRegistry() {
    }

    public static ITypeCheckErrorReporter get() {
        return errorHelper;
    }

    public static void set(ITypeCheckErrorReporter newErrorHelper) {
        errorHelper = newErrorHelper;
    }
}
