package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;

public interface ICaseInsensitiveScope extends IScope
{

    boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol);
}
