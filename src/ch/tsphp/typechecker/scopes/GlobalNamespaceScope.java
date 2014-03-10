/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.scopes;

import ch.tsphp.common.ILowerCaseStringMap;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.LowerCaseStringMap;
import ch.tsphp.typechecker.utils.MapHelper;

import java.util.List;

public class GlobalNamespaceScope extends AScope implements IGlobalNamespaceScope
{

    private final ILowerCaseStringMap<List<ISymbol>> symbolsCaseInsensitive = new LowerCaseStringMap<>();

    public GlobalNamespaceScope(IScopeHelper scopeHelper, String scopeName) {
        super(scopeHelper, scopeName, null);
    }

    @Override
    public void define(ISymbol symbol) {
        scopeHelper.define(this, symbol);
        MapHelper.addToListMap(symbolsCaseInsensitive, symbol.getName(), symbol);
    }

    @Override
    public boolean doubleDefinitionCheck(ISymbol symbol) {
        return scopeHelper.checkIsNotDoubleDefinition(symbols, symbol);
    }


    @Override
    public boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol) {
        return scopeHelper.checkIsNotDoubleDefinition(symbolsCaseInsensitive, symbol);
    }

    @Override
    public ISymbol resolve(ITSPHPAst typeAst) {
        ISymbol symbol = null;
        String typeName = getTypeNameWithoutNamespacePrefix(typeAst.getText());
        if (symbols.containsKey(typeName)) {
            symbol = symbols.get(typeName).get(0);
        }
        return symbol;
    }

    //Warning! start code duplication - same as in MethodSymbol
    @Override
    public boolean isFullyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && initialisedSymbols.get(symbolName);
    }

    @Override
    public boolean isPartiallyInitialised(ISymbol symbol) {
        String symbolName = symbol.getName();
        return initialisedSymbols.containsKey(symbolName) && !initialisedSymbols.get(symbolName);
    }
    //Warning! end code duplication - same as in MethodSymbol

    private String getTypeNameWithoutNamespacePrefix(String typeName) {
        int scopeNameLength = scopeName.length();
        if (typeName.length() > scopeNameLength && typeName.substring(0, scopeNameLength).equals(scopeName)) {
            typeName = typeName.substring(scopeNameLength);
        }
        return typeName;
    }

    @Override
    public ITypeSymbol getTypeSymbolWhichClashesWithUse(ITSPHPAst identifier) {
        String typeName = identifier.getText();
        if (typeName.contains("\\")) {
            throw new IllegalArgumentException("identifier contained \\ - "
                    + "do not use this method other than with the right identifier of an use statement.");
        }
        ITypeSymbol typeSymbol = null;
        if (symbolsCaseInsensitive.containsKey(typeName)) {
            typeSymbol = (ITypeSymbol) symbolsCaseInsensitive.get(typeName).get(0);
        }
        return typeSymbol;
    }
}
