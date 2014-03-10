/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.IAstHelper;
import ch.tsphp.typechecker.symbols.ITypeSymbolWithPHPBuiltInCasting;

public class BuiltInCastingMethod extends ACastingMethod
{

    public BuiltInCastingMethod(IAstHelper theAstHelper, ITypeSymbolWithPHPBuiltInCasting theType) {
        super(theAstHelper, theType);
    }

    @Override
    protected int getTokenType() {
        return ((ITypeSymbolWithPHPBuiltInCasting) typeSymbol).getTokenTypeForCasting();
    }
}
