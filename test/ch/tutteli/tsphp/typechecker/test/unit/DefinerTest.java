package ch.tutteli.tsphp.typechecker.test.unit;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.Definer;
import ch.tutteli.tsphp.typechecker.IDefiner;
import ch.tutteli.tsphp.typechecker.scopes.IConditionalScope;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.symbols.IAliasSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class DefinerTest
{

    private ISymbolFactory symbolFactory;
    private IScopeFactory scopeFactory;

    private IGlobalNamespaceScope globalDefaultScope;


    @Before
    public void setUp() {
        symbolFactory = mock(ISymbolFactory.class);
        scopeFactory = mock(IScopeFactory.class);
    }

    @Test
    public void getGlobalDefaultNamespace_FirstCall_UsesScopeFactory() {
        initScopeFactoryForGlobalDefaultNamespace();

        IDefiner definer = createDefiner();
        IGlobalNamespaceScope scope = definer.getGlobalDefaultNamespace();

        assertThat(scope, is(globalDefaultScope));
        verify(scopeFactory).createGlobalNamespaceScope("\\");
    }

    @Test
    public void getGlobalDefaultNamespace_SecondCall_UsesScopeFactoryOnlyOnce() {
        initScopeFactoryForGlobalDefaultNamespace();

        IDefiner definer = createDefiner();
        IGlobalNamespaceScope scope = definer.getGlobalDefaultNamespace();

        assertThat(scope, is(globalDefaultScope));
        verify(scopeFactory, times(1)).createGlobalNamespaceScope("\\");

        scope = definer.getGlobalDefaultNamespace();

        assertThat(scope, is(globalDefaultScope));
        verify(scopeFactory, times(1)).createGlobalNamespaceScope("\\");
    }

    @Test
    public void defineNamespace_FirstCall_UsesScopeFactory() {
        NamespaceAndGlobalPair namespaceAndGlobal = initDefineNamespace("name");

        IDefiner definer = createDefiner();
        INamespaceScope namespaceScope = definer.defineNamespace("name");

        assertThat(namespaceScope, is(namespaceAndGlobal.namespaceScope));
        verify(scopeFactory, times(1)).createGlobalNamespaceScope("name");
        verify(scopeFactory).createNamespace("name", namespaceAndGlobal.globalNamespaceScope);
    }

    @Test
    public void defineNamespace_SecondCall_GlobalNamespaceOnlyCreatedOnce() {
        NamespaceAndGlobalPair namespaceAndGlobal = initDefineNamespace("name");
        INamespaceScope secondNamespaceScope = mock(INamespaceScope.class);
        when(scopeFactory.createNamespace("name", namespaceAndGlobal.globalNamespaceScope))
                .thenReturn(namespaceAndGlobal.namespaceScope)
                .thenReturn(secondNamespaceScope);

        IDefiner definer = createDefiner();
        INamespaceScope namespaceScope1 = definer.defineNamespace("name");
        INamespaceScope namespaceScope2 = definer.defineNamespace("name");

        assertThat(namespaceScope1, is(namespaceAndGlobal.namespaceScope));
        assertThat(namespaceScope2, is(secondNamespaceScope));

        verify(scopeFactory, times(1)).createGlobalNamespaceScope("name");
        verify(scopeFactory, times(2)).createNamespace("name", namespaceAndGlobal.globalNamespaceScope);
    }

    @Test
    public void getGlobalNamespaceScopes_NothingDefined_ContainsGlobalDefaultNamespace() {
        initScopeFactoryForGlobalDefaultNamespace();

        IDefiner definer = createDefiner();
        ILowerCaseStringMap<IGlobalNamespaceScope> scopes = definer.getGlobalNamespaceScopes();

        assertThat(scopes, hasEntry("\\", globalDefaultScope));
        verify(scopeFactory, times(1)).createGlobalNamespaceScope("\\");
    }

    @Test
    public void getGlobalNamespaceScopes_DefineAdditionalNamespace_ContainsBoth() {
        NamespaceAndGlobalPair namespaceAndGlobal = initDefineNamespace("name");

        IDefiner definer = createDefiner();
        definer.defineNamespace("name");
        ILowerCaseStringMap<IGlobalNamespaceScope> scopes = definer.getGlobalNamespaceScopes();

        assertThat(scopes, hasEntry("\\", globalDefaultScope));
        assertThat(scopes, hasEntry("name", namespaceAndGlobal.globalNamespaceScope));
    }

    @Test
    public void defineUse_standard_SetScopeForTypeAndCreateAliasSymbolAndDefineIt() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst typeAst = mock(ITSPHPAst.class);
        ITSPHPAst aliasAst = mock(ITSPHPAst.class);
        when(aliasAst.getText()).thenReturn("alias");
        IAliasSymbol aliasSymbol = mock(IAliasSymbol.class);
        when(symbolFactory.createAliasSymbol(aliasAst, "alias")).thenReturn(aliasSymbol);

        IDefiner definer = createDefiner();
        definer.defineUse(namespaceScope, typeAst, aliasAst);

        verify(typeAst).setScope(namespaceScope);
        verify(aliasAst).setSymbol(aliasSymbol);
        verify(aliasAst).setScope(namespaceScope);
        verify(namespaceScope).defineUse(aliasSymbol);
        verify(symbolFactory).createAliasSymbol(aliasAst, "alias");
    }

    @Test
    public void defineVariable_standard_SetScopeForTypeAndCreateVariableSymbolAndDefineIt() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst modifierAst = mock(ITSPHPAst.class);
        ITSPHPAst typeAst = mock(ITSPHPAst.class);
        ITSPHPAst identifierAst = mock(ITSPHPAst.class);
        IVariableSymbol variableSymbol = mock(IVariableSymbol.class);
        when(symbolFactory.createVariableSymbol(modifierAst, identifierAst)).thenReturn(variableSymbol);

        IDefiner definer = createDefiner();
        definer.defineVariable(namespaceScope, modifierAst, typeAst, identifierAst);

        verify(typeAst).setScope(namespaceScope);
        verifyScopeSymbolAndDefine(namespaceScope, identifierAst, variableSymbol);
        verify(symbolFactory).createVariableSymbol(modifierAst, identifierAst);
    }

    @Test
    public void defineConstant_standard_SetScopeForTypeAndCreateVariableSymbolAndDefineIt() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst modifierAst = mock(ITSPHPAst.class);
        ITSPHPAst typeAst = mock(ITSPHPAst.class);
        ITSPHPAst identifierAst = mock(ITSPHPAst.class);
        IVariableSymbol variableSymbol = mock(IVariableSymbol.class);
        when(symbolFactory.createVariableSymbol(modifierAst, identifierAst)).thenReturn(variableSymbol);

        IDefiner definer = createDefiner();
        definer.defineConstant(namespaceScope, modifierAst, typeAst, identifierAst);

        verify(typeAst).setScope(namespaceScope);
        verifyScopeSymbolAndDefine(namespaceScope, identifierAst, variableSymbol);
        verify(symbolFactory).createVariableSymbol(modifierAst, identifierAst);
    }

    @Test
    public void defineInterface_NoExtends_SetScopeForIdentifierAndCreateInterfaceSymbolAndDefineIt() {
        defineInterface(mock(ITSPHPAst.class));
    }

    private INamespaceScope defineInterface(ITSPHPAst extendsAst) {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst modifierAst = mock(ITSPHPAst.class);
        ITSPHPAst identifierAst = mock(ITSPHPAst.class);
        IInterfaceTypeSymbol interfaceTypeSymbol = mock(IInterfaceTypeSymbol.class);
        when(symbolFactory.createInterfaceTypeSymbol(modifierAst, identifierAst, namespaceScope))
                .thenReturn(interfaceTypeSymbol);

        IDefiner definer = createDefiner();
        IInterfaceTypeSymbol interfaceSymbol = definer.defineInterface(namespaceScope,
                modifierAst, identifierAst, extendsAst);

        assertThat(interfaceSymbol, is(interfaceTypeSymbol));
        verifyScopeSymbolAndDefine(namespaceScope, identifierAst, interfaceTypeSymbol);
        verify(symbolFactory).createInterfaceTypeSymbol(modifierAst, identifierAst, namespaceScope);
        return namespaceScope;
    }

    @Test
    public void defineInterface_OneExtends_SetScopeForIdentifierAndParentTypeCreateInterfaceSymbolAndDefineIt() {
        ITSPHPAst extendsAst = mock(ITSPHPAst.class);
        when(extendsAst.getChildCount()).thenReturn(1);
        ITSPHPAst parentType = addParentType(extendsAst, 0);

        INamespaceScope namespaceScope = defineInterface(extendsAst);

        verify(parentType).setScope(namespaceScope);
    }

    @Test
    public void defineInterface_MultipleExtends_SetScopeForIdentifierAndParentTypesCreateInterfaceSymbolAndDefineIt() {
        ITSPHPAst extendsAst = mock(ITSPHPAst.class);
        when(extendsAst.getChildCount()).thenReturn(2);
        ITSPHPAst parentType = addParentType(extendsAst, 0);
        ITSPHPAst parentType2 = addParentType(extendsAst, 1);

        INamespaceScope namespaceScope = defineInterface(extendsAst);

        verify(parentType).setScope(namespaceScope);
        verify(parentType2).setScope(namespaceScope);
    }

    private ITSPHPAst addParentType(ITSPHPAst identifierList, int index) {
        ITSPHPAst parentType = mock(ITSPHPAst.class);
        when(identifierList.getChild(index)).thenReturn(parentType);
        return parentType;
    }

    @Test
    public void defineClass_NoExtendsNoImplements_SetScopeForIdentifierAndCreateClassSymbolAndDefineIt() {
        defineClass(mock(ITSPHPAst.class), mock(ITSPHPAst.class));
    }

    private INamespaceScope defineClass(ITSPHPAst extendsAst, ITSPHPAst implementsAst) {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst modifierAst = mock(ITSPHPAst.class);
        ITSPHPAst identifierAst = mock(ITSPHPAst.class);

        IClassTypeSymbol classTypeSymbol = mock(IClassTypeSymbol.class);
        when(symbolFactory.createClassTypeSymbol(modifierAst, identifierAst, namespaceScope))
                .thenReturn(classTypeSymbol);

        IDefiner definer = createDefiner();
        IClassTypeSymbol classSymbol = definer.defineClass(namespaceScope, modifierAst, identifierAst,
                extendsAst, implementsAst);

        assertThat(classSymbol, is(classTypeSymbol));
        verifyScopeSymbolAndDefine(namespaceScope, identifierAst, classTypeSymbol);
        verify(symbolFactory).createClassTypeSymbol(modifierAst, identifierAst, namespaceScope);
        return namespaceScope;
    }

    @Test
    public void defineClass_OneExtendsNoImplements_SetScopeForIdentifierAndCreateClassSymbolAndDefineIt() {
        ITSPHPAst extendsAst = mock(ITSPHPAst.class);
        when(extendsAst.getChildCount()).thenReturn(1);
        ITSPHPAst parentType = addParentType(extendsAst, 0);

        INamespaceScope namespaceScope = defineClass(extendsAst, mock(ITSPHPAst.class));

        verify(parentType).setScope(namespaceScope);
    }

    @Test
    public void defineClass_MultipleExtendsNoImplements_SetScopeForIdentifierAndCreateClassSymbolAndDefineIt() {
        ITSPHPAst extendsAst = mock(ITSPHPAst.class);
        when(extendsAst.getChildCount()).thenReturn(2);
        ITSPHPAst parentType = addParentType(extendsAst, 0);
        ITSPHPAst parentType2 = addParentType(extendsAst, 1);

        INamespaceScope namespaceScope = defineClass(extendsAst, mock(ITSPHPAst.class));

        verify(parentType).setScope(namespaceScope);
        verify(parentType2).setScope(namespaceScope);
    }

    @Test
    public void defineClass_NoExtendsOneImplements_SetScopeForIdentifierAndCreateClassSymbolAndDefineIt() {
        ITSPHPAst implementsAst = mock(ITSPHPAst.class);
        when(implementsAst.getChildCount()).thenReturn(1);
        ITSPHPAst parentType = addParentType(implementsAst, 0);

        INamespaceScope namespaceScope = defineClass(mock(ITSPHPAst.class), implementsAst);

        verify(parentType).setScope(namespaceScope);
    }


    @Test
    public void defineClass_NoExtendsMultipleImplements_SetScopeForIdentifierAndCreateClassSymbolAndDefineIt() {
        ITSPHPAst implementsAst = mock(ITSPHPAst.class);
        when(implementsAst.getChildCount()).thenReturn(2);
        ITSPHPAst parentType = addParentType(implementsAst, 0);
        ITSPHPAst parentType2 = addParentType(implementsAst, 1);

        INamespaceScope namespaceScope = defineClass(mock(ITSPHPAst.class), implementsAst);

        verify(parentType).setScope(namespaceScope);
        verify(parentType2).setScope(namespaceScope);
    }


    @Test
    public void defineClass_MultipleExtendsMultipleImplements_SetScopeForIdentifierAndCreateClassSymbolAndDefineIt() {
        ITSPHPAst extendsAst = mock(ITSPHPAst.class);
        when(extendsAst.getChildCount()).thenReturn(2);
        ITSPHPAst parentType = addParentType(extendsAst, 0);
        ITSPHPAst parentType2 = addParentType(extendsAst, 1);

        ITSPHPAst implementsAst = mock(ITSPHPAst.class);
        when(implementsAst.getChildCount()).thenReturn(2);
        ITSPHPAst implParentType = addParentType(implementsAst, 0);
        ITSPHPAst implParentType2 = addParentType(implementsAst, 1);

        INamespaceScope namespaceScope = defineClass(extendsAst, implementsAst);

        verify(parentType).setScope(namespaceScope);
        verify(parentType2).setScope(namespaceScope);
        verify(implParentType).setScope(namespaceScope);
        verify(implParentType2).setScope(namespaceScope);
    }

    @Test
    public void defineMethod_standard_SetScopeForIdentifierAndReturnTypeAndCreateMethodSymbolAndDefineIt() {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        ITSPHPAst modifierAst = mock(ITSPHPAst.class);
        ITSPHPAst returnTypeModifierAst = mock(ITSPHPAst.class);
        ITSPHPAst returnTypeAst = mock(ITSPHPAst.class);
        ITSPHPAst identifierAst = mock(ITSPHPAst.class);

        IMethodSymbol expectedMethodSymbol = mock(IMethodSymbol.class);
        when(symbolFactory.createMethodSymbol(modifierAst, returnTypeModifierAst, identifierAst, namespaceScope))
                .thenReturn(expectedMethodSymbol);

        IDefiner definer = createDefiner();
        IMethodSymbol methodSymbol = definer.defineMethod(namespaceScope, modifierAst, returnTypeModifierAst,
                returnTypeAst, identifierAst);

        assertThat(methodSymbol, is(expectedMethodSymbol));
        verify(returnTypeAst).setScope(namespaceScope);
        verifyScopeSymbolAndDefine(namespaceScope, identifierAst, expectedMethodSymbol);
        verify(symbolFactory).createMethodSymbol(modifierAst, returnTypeModifierAst, identifierAst, namespaceScope);
    }

    @Test
    public void defineConditionalScope_standard_CallScopeFactory() {
        IScope parentScope = mock(IScope.class);
        IConditionalScope conditionalScope = mock(IConditionalScope.class);
        when(scopeFactory.createConditionalScope(parentScope)).thenReturn(conditionalScope);

        IDefiner definer = createDefiner();
        IConditionalScope scope = definer.defineConditionalScope(parentScope);

        assertThat(scope, is(conditionalScope));
        verify(scopeFactory).createConditionalScope(parentScope);
        //shouldn't have any additional interaction with scopeFactory than creating the global default namespace
        verify(scopeFactory).createGlobalNamespaceScope("\\");
        verifyNoMoreInteractions(scopeFactory);
    }

    private IDefiner createDefiner() {
        return new Definer(symbolFactory, scopeFactory);
    }

    private void verifyScopeSymbolAndDefine(IScope scope, ITSPHPAst identifierAst, ISymbol symbol) {
        verify(identifierAst).setSymbol(symbol);
        verify(identifierAst).setScope(scope);
        verify(scope).define(symbol);
    }

    private void initScopeFactoryForGlobalDefaultNamespace() {
        globalDefaultScope = mock(IGlobalNamespaceScope.class);
        when(scopeFactory.createGlobalNamespaceScope("\\")).thenReturn(globalDefaultScope);
    }

    private NamespaceAndGlobalPair initDefineNamespace(String name) {
        INamespaceScope namespaceScope = mock(INamespaceScope.class);
        IGlobalNamespaceScope globalScope = mock(IGlobalNamespaceScope.class);
        when(scopeFactory.createNamespace(name, globalScope)).thenReturn(namespaceScope);
        when(scopeFactory.createGlobalNamespaceScope(name)).thenReturn(globalScope);

        return new NamespaceAndGlobalPair(globalScope, namespaceScope);
    }

    private class NamespaceAndGlobalPair
    {
        public IGlobalNamespaceScope globalNamespaceScope;
        public INamespaceScope namespaceScope;

        private NamespaceAndGlobalPair(IGlobalNamespaceScope globalNamespaceScope, INamespaceScope namespaceScope) {
            this.globalNamespaceScope = globalNamespaceScope;
            this.namespaceScope = namespaceScope;
        }
    }
}
