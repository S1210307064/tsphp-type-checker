/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.ISymbol;
import ch.tsphp.common.symbols.ITypeSymbol;
import ch.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.IVariableSymbol;

/**
 * Responsible to resolve global symbols.
 * <p/>
 * That means, it does not resolve fields or methods but symbols such as namespaces, constants, classes etc.
 */
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
