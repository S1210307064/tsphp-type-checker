package ch.tsphp.typechecker;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ISymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;

public interface ISymbolResolver
{

    boolean isAbsolute(String typeName);

    IClassTypeSymbol getEnclosingClass(ITSPHPAst ast);

    IMethodSymbol getEnclosingMethod(ITSPHPAst ast);

    IScope getEnclosingGlobalNamespaceScope(IScope scope);

    ISymbol resolveGlobalIdentifier(ITSPHPAst typeAst);

    ISymbol resolveGlobalIdentifierWithFallback(ITSPHPAst ast);

    ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias);

    IVariableSymbol resolveConstant(ITSPHPAst ast);
}
