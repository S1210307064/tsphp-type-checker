package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.ISymbol;

import java.util.Set;

public interface ISymbolWithModifier extends ISymbol
{

    void addModifier(Integer modifier);

    boolean removeModifier(Integer modifier);

    Set<Integer> getModifiers();

    void setModifiers(Set<Integer> modifier);
}
