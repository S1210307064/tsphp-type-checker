package ch.tutteli.tsphp.typechecker.scopes;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.List;

public class GlobalNamespaceScope extends AScope implements IGlobalNamespaceScope
{

    private ILowerCaseStringMap<List<ISymbol>> symbolsCaseInsensitive = new LowerCaseStringMap<>();

    public GlobalNamespaceScope(IScopeHelper scopeHelper, String scopeName) {
        super(scopeHelper, scopeName, null);
    }

    @Override
    public void define(ISymbol symbol) {
        super.define(symbol);
        MapHelper.addToListMap(symbolsCaseInsensitive, symbol.getName(), symbol);
    }

    @Override
    public boolean doubleDefinitionCheckCaseInsensitive(ISymbol symbol) {
        return scopeHelper.doubleDefinitionCheck(symbolsCaseInsensitive, symbol);
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

    @Override
    public String getTypeNameWithoutNamespacePrefix(String typeName) {
        int scopeNameLenght = scopeName.length();
        if (typeName.length() > scopeNameLenght && typeName.substring(0, scopeNameLenght).equals(scopeName)) {
            typeName = typeName.substring(scopeNameLenght);
        }
        return typeName;
    }
}
