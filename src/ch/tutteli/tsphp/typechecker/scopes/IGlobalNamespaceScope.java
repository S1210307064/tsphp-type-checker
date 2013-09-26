package ch.tutteli.tsphp.typechecker.scopes;

public interface IGlobalNamespaceScope extends ICaseInsensitiveScope
{

    String getTypeNameWithoutNamespacePrefix(String typeName);
}
