/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.utils;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.CastingDto;

/**
 * Provides helper methods which can be used to modify an AST
 */
public interface ITypeCheckerAstHelper
{
    /**
     * Create a function call or static method call AST node and use the given expression as actual parameter.
     *
     * @return The create node
     */
    ITSPHPAst prependCasting(CastingDto parameterPromotionDto);
}
