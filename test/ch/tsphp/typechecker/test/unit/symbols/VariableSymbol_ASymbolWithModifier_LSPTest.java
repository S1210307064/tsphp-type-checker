/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class VariableSymbol_ASymbolWithModifier_LSPTest from the TinsPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.symbols.ASymbolWithModifier;
import ch.tsphp.typechecker.symbols.VariableSymbol;

public class VariableSymbol_ASymbolWithModifier_LSPTest extends ASymbolWithModifierTest
{

    @Override
    protected ASymbolWithModifier createSymbolWithModifier(ITSPHPAst definitionAst, IModifierSet modifiers,
            String name) {
        return new VariableSymbol(definitionAst, modifiers, name);
    }
}
