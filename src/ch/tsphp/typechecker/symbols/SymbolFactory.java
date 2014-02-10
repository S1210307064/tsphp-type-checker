package ch.tsphp.typechecker.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeSymbol;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.symbols.erroneous.ErroneousMethodSymbol;
import ch.tsphp.typechecker.symbols.erroneous.ErroneousTypeSymbol;
import ch.tsphp.typechecker.symbols.erroneous.ErroneousVariableSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousMethodSymbol;
import ch.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SymbolFactory implements ISymbolFactory
{
    private final IScopeHelper scopeHelper;
    private ITypeSymbol objectTypeSymbol = null;

    public SymbolFactory(IScopeHelper theScopeHelper) {
        scopeHelper = theScopeHelper;
    }

    @Override
    public void setObjectTypeSymbol(ITypeSymbol typeSymbol) {
        objectTypeSymbol = typeSymbol;
    }

    @Override
    public INullTypeSymbol createNullTypeSymbol() {
        return new NullTypeSymbol();

    }

    @Override
    public IVoidTypeSymbol createVoidTypeSymbol() {
        return new VoidTypeSymbol();

    }

    @Override
    public IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenTypeForCasting, ITypeSymbol parentTypeSymbol,
            boolean isNullable, int defaultValueTokenType, String defaultValue) {
        return new ScalarTypeSymbol(
                name,
                parentTypeSymbol,
                tokenTypeForCasting,
                isNullable,
                defaultValueTokenType,
                defaultValue);
    }

    @Override
    public IScalarTypeSymbol createScalarTypeSymbol(String name, int tokenTypeForCasting,
            Set<ITypeSymbol> parentTypeSymbol, boolean isNullable, int defaultValueTokenType, String defaultValue) {
        return new ScalarTypeSymbol(
                name,
                parentTypeSymbol,
                tokenTypeForCasting,
                isNullable,
                defaultValueTokenType,
                defaultValue);
    }

    @Override
    public IArrayTypeSymbol createArrayTypeSymbol(String name, int tokenType,
            ITypeSymbol keyValue, ITypeSymbol valueType) {
        return new ArrayTypeSymbol(name, tokenType, keyValue, valueType, objectTypeSymbol);
    }

    @Override
    public IPseudoTypeSymbol createPseudoTypeSymbol(String name) {
        return new PseudoTypeSymbol(name, objectTypeSymbol);
    }

    @Override
    public IAliasSymbol createAliasSymbol(ITSPHPAst useDefinition, String alias) {
        return new AliasSymbol(useDefinition, alias);
    }

    @Override
    public IAliasTypeSymbol createAliasTypeSymbol(ITSPHPAst definitionAst, String name) {
        return new AliasTypeSymbol(definitionAst, name, objectTypeSymbol);
    }

    @Override
    public IInterfaceTypeSymbol createInterfaceTypeSymbol(ITSPHPAst modifier, ITSPHPAst identifier,
            IScope currentScope) {
        return new InterfaceTypeSymbol(
                scopeHelper,
                identifier,
                getModifiers(modifier),
                identifier.getText(),
                currentScope,
                objectTypeSymbol);
    }

    @Override
    public IClassTypeSymbol createClassTypeSymbol(ITSPHPAst classModifierAst, ITSPHPAst identifier,
            IScope currentScope) {
        return new ClassTypeSymbol(
                scopeHelper,
                identifier,
                getModifiers(classModifierAst),
                identifier.getText(),
                currentScope,
                objectTypeSymbol);
    }

    @Override
    public IMethodSymbol createMethodSymbol(ITSPHPAst methodModifier, ITSPHPAst returnTypeModifier,
            ITSPHPAst identifier, IScope currentScope) {
        return new MethodSymbol(
                scopeHelper,
                identifier,
                getModifiers(methodModifier),
                getModifiers(returnTypeModifier),
                identifier.getText(),
                currentScope);
    }

    @Override
    public IVariableSymbol createThisSymbol(ITSPHPAst variableId, IPolymorphicTypeSymbol polymorphicTypeSymbol) {
        return new ThisSymbol(variableId, variableId.getText(), polymorphicTypeSymbol);
    }

    @Override
    public IVariableSymbol createVariableSymbol(ITSPHPAst typeModifier, ITSPHPAst variableId) {
        Set<Integer> modifiers = typeModifier != null ? getModifiers(typeModifier) : new HashSet<Integer>();
        return new VariableSymbol(variableId, modifiers, variableId.getText());
    }

    @Override
    public IErroneousTypeSymbol createErroneousTypeSymbol(ITSPHPAst ast, TSPHPException exception) {
        IMethodSymbol methodSymbol = createErroneousMethodSymbol(ast, exception);
        return new ErroneousTypeSymbol(ast, exception, methodSymbol);
    }

    @Override
    public IErroneousMethodSymbol createErroneousMethodSymbol(ITSPHPAst ast, TSPHPException ex) {
        return new ErroneousMethodSymbol(ast, ex);
    }

    @Override
    public IVariableSymbol createErroneousVariableSymbol(ITSPHPAst ast, TSPHPException exception) {
        return new ErroneousVariableSymbol(ast, exception);
    }

    private Set<Integer> getModifiers(ITSPHPAst modifierAst) {
        Set<Integer> modifiers = new HashSet<>();

        List<ITSPHPAst> children = modifierAst.getChildren();
        if (children != null && !children.isEmpty()) {
            for (ITSPHPAst child : children) {
                modifiers.add(child.getType());
            }
        }
        return modifiers;
    }
}