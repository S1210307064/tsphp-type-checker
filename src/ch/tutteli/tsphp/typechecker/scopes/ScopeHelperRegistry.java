package ch.tutteli.tsphp.typechecker.scopes;

public final class ScopeHelperRegistry
{

    private static IScopeHelper scopeHelper;

    private ScopeHelperRegistry() {
    }

    public static IScopeHelper get() {
        return scopeHelper;
    }

    public static void set(IScopeHelper newScopeHelper) {
        scopeHelper = newScopeHelper;
    }
}
