package ch.tutteli.tsphp.typechecker.utils;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.CastingDto;

public interface ITypeCheckerAstHelper
{
    /**
     * Create a function call or static method call AST node and use the given expression as actual parameter.
     *
     * @return The create node
     */
    ITSPHPAst prependCasting(CastingDto parameterPromotionDto);
}
