package ch.tutteli.tsphp.typechecker.symbols;

public interface ISymbolWithAccessModifier extends ISymbolWithModifier
{

    boolean isPublic();

    boolean isProtected();

    boolean isPrivate();

    boolean canBeAccessedFrom(int type);
}
