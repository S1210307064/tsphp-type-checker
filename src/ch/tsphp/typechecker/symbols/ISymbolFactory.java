package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;

import java.util.Set;

public interface ISymbolFactory
{

    void setObjectTypeSymbol(ITypeSymbol typeSymbol);

    INullTypeSymbol createNullTypeSymbol();

    IVoidTypeSymbol createVoidTypeSymbol();

    IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenType, ITypeSymbol parentTypeSymbol,
            boolean isNullable, int defaultValueTokenType, String defaultValue);

    IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenType, Set<ITypeSymbol> parentTypeSymbol,
            boolean isNullable, int defaultValueTokenType, String defaultValue);

    IArrayTypeSymbol createArrayTypeSymbol(String name, int tokenType, ITypeSymbol keyValue, ITypeSymbol valueType);

    IPseudoTypeSymbol createPseudoTypeSymbol(String name);

    IAliasSymbol createAliasSymbol(ITSPHPAst useDefinition, String alias);

    IAliasTypeSymbol createAliasTypeSymbol(ITSPHPAst definitionAst, String name);

    IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier, IScope currentScope);

    IClassTypeSymbol createClassTypeSymbol(ITSPHPAst classModifier, ITSPHPAst identifier, IScope currentScope);

    IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier, ITSPHPAst identifier,
            IScope currentScope);

    IVariableSymbol createThisSymbol(ITSPHPAst variableId, IPolymorphicTypeSymbol polymorphicTypeSymbol);

    IVariableSymbol createVariableSymbol(ITSPHPAst typeModifier, ITSPHPAst variableId);

    IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPAst ast, TSPHPException exception);

    IErroneousMethodSymbol createErroneousMethodSymbol(ITSPHPAst ast, TSPHPException exception);

    IVariableSymbol createErroneousVariableSymbol(ITSPHPAst ast, TSPHPException exception);
}
