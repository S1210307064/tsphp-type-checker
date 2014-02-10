package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;

public interface ICaseInsensitiveScope extends IScope
{

    boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol);
}
