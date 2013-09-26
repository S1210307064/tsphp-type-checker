package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;

public interface ISymbolResolver
{

    boolean isAbsolute(String typeName);

    IClassTypeSymbol getEnclosingClass(ITSPHPAst ast);
    
    IMethodSymbol getEnclosingMethod(ITSPHPAst ast);

    IScope getEnclosingGlobalNamespaceScope(IScope scope);

    IScope getResolvingScope(ITSPHPAst typeAst);

    ISymbol resolveGlobalIdentifier(ITSPHPAst typeAst);

    ISymbol resolveGlobalIdentifierWithFallback(ITSPHPAst ast);

    ISymbol resolveInClassSymbol(ITSPHPAst ast);

    ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias);

    IVariableSymbol resolveConstant(ITSPHPAst ast);
}
