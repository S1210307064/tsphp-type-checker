/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;

import java.util.Set;

/**
 * Represents a type which can hold the value null.
 */
public abstract class ANullableTypeSymbol extends ATypeSymbol
{

    public ANullableTypeSymbol(String name, ITypeSymbol parentTypeSymbol) {
        super(null, name, parentTypeSymbol);
        //make sure nullable is part of the modifiers
        addModifier(TSPHPDefinitionWalker.QuestionMark);
    }

    public ANullableTypeSymbol(String name, Set<ITypeSymbol> parentTypeSymbols) {
        super(null, name, parentTypeSymbols);
        //make sure nullable is part of the modifiers
        addModifier(TSPHPDefinitionWalker.QuestionMark);
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.Null, "null");
    }

    @Override
    public void setModifiers(IModifierSet newModifiers) {
        super.setModifiers(newModifiers);
        //make sure nullable is part of the modifiers
        modifiers.add(TSPHPDefinitionWalker.QuestionMark);
    }
}
