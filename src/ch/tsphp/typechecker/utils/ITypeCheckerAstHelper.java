package ch.tsphp.typechecker.utils;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.CastingDto;

public interface ITypeCheckerAstHelper
{
    /**
     * Create a function call or static method call AST node and use the given expression as actual parameter.
     *
     * @return The create node
     */
    ITSPHPAst prependCasting(CastingDto parameterPromotionDto);
}
