/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.ILowerCaseStringMap;
import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.LowerCaseStringMap;
import ch.tutteli.tsphp.common.exceptions.DefinitionException;
import ch.tutteli.tsphp.common.exceptions.ReferenceException;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeFactory;
import ch.tutteli.tsphp.typechecker.symbols.IAliasTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ICanBeStatic;
import ch.tutteli.tsphp.typechecker.symbols.IClassTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IInterfaceTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tutteli.tsphp.typechecker.symbols.IPolymorphicTypeSymbol;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousSymbol;
import ch.tutteli.tsphp.typechecker.symbols.erroneous.IErroneousTypeSymbol;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class SymbolTable implements ISymbolTable
{

    private ISymbolFactory symbolFactory;
    private ILowerCaseStringMap<IGlobalNamespaceScope> globalNamespaceScopes = new LowerCaseStringMap<>();
    private IGlobalNamespaceScope globalDefaultNamespace;
    private IResolver resolver;
    private IDefiner definer;

    public SymbolTable(ISymbolFactory aSymbolFactory, IScopeFactory aScopeFactory, ITSPHPAstAdaptor theAstAdaptor) {
        symbolFactory = aSymbolFactory;

        definer = new Definer(aSymbolFactory, aScopeFactory, theAstAdaptor, globalNamespaceScopes);
        definer.initTypeSystem();
        globalDefaultNamespace = definer.getGlobalDefaultNamespace();
        resolver = new Resolver(aSymbolFactory, globalNamespaceScopes, globalDefaultNamespace);
    }

    @Override
    public IDefiner getDefiner() {
        return definer;
    }
    
    @Override
    public ILowerCaseStringMap<IGlobalNamespaceScope>  getGlobalNamespaceScopes(){
        return globalNamespaceScopes;
    }
    
    @Override
    public boolean checkIfInterface(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isInterface = symbol instanceof IInterfaceTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isInterface) {
            ErrorReporterRegistry.get().interfaceExpected(typeAst);
        }
        return isInterface;
    }

    @Override
    public boolean checkIfClass(ITSPHPAst typeAst, ITypeSymbol symbol) {
        boolean isClass = symbol instanceof IClassTypeSymbol || symbol instanceof IErroneousTypeSymbol;
        if (!isClass) {
            ErrorReporterRegistry.get().classExpected(typeAst);
        }
        return isClass;
    }

    @Override
    public boolean checkForwardReference(ITSPHPAst ast) {
        ISymbol symbol = ast.getSymbol();
        boolean isNotUsedBefore = true;
        //only check if not already an error occured in conjunction with this ast (for instance missing declaration)
        if (!(symbol instanceof IErroneousSymbol)) {
            ITSPHPAst definitionAst = symbol.getDefinitionAst();
            isNotUsedBefore = definitionAst.isDefinedEarlierThan(ast);
            if (!isNotUsedBefore) {
                DefinitionException exception = ErrorReporterRegistry.get().forwardReference(ast, definitionAst);
                symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
                ast.setSymbol(symbol);
            }
        }
        return isNotUsedBefore;
    }

    @Override
    public IVariableSymbol resolveConstant(ITSPHPAst ast) {
        IVariableSymbol symbol = resolver.resolveConstant(ast);

        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return symbol;
    }

    @Override
    public IMethodSymbol resolveStaticMethod(ITSPHPAst callee, ITSPHPAst id) {
        IMethodSymbol symbol;
        ISymbol calleeSymbol = callee.getSymbol();
        if (!(calleeSymbol instanceof IErroneousSymbol)) {
            symbol = (IMethodSymbol) resolveStatic(callee, id);
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) calleeSymbol;
            symbol = symbolFactory.createErroneousMethodSymbol(id, erroneousSymbol.getException());
        }
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(id);
            symbol = symbolFactory.createErroneousMethodSymbol(id, exception);
        }
        return symbol;
    }

    private ICanBeStatic resolveStatic(ITSPHPAst callee, ITSPHPAst id) {
        IClassTypeSymbol classTypeSymbol = (IClassTypeSymbol) callee.getSymbol();
        ICanBeStatic symbol = (ICanBeStatic) classTypeSymbol.resolve(id);
        if (isDefinedButNotStatic(symbol)) {
            ErrorReporterRegistry.get().notStatic(callee);
        }
        return symbol;
    }

    private boolean isDefinedButNotStatic(ICanBeStatic symbol) {
        return symbol != null && !symbol.isStatic();
    }

    @Override
    public IVariableSymbol resolveStaticMemberOrClassConstant(ITSPHPAst accessor, ITSPHPAst id) {
        IVariableSymbol symbol;
        ISymbol accessorSymbol = accessor.getSymbol();
        if (!(accessorSymbol instanceof IErroneousSymbol)) {
            symbol = (IVariableSymbol) resolveStatic(accessor, id);
        } else {
            IErroneousSymbol erroneousSymbol = (IErroneousSymbol) accessorSymbol;
            symbol = symbolFactory.createErroneousVariableSymbol(id, erroneousSymbol.getException());
        }
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(id);
            symbol = symbolFactory.createErroneousVariableSymbol(id, exception);
        }
        return symbol;
    }

    @Override
    public IMethodSymbol resolveFunction(ITSPHPAst ast) {
        ISymbol symbol = resolver.resolveGlobalIdentifierWithFallback(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousMethodSymbol(ast, exception);
        }
        return (IMethodSymbol) symbol;
    }

    @Override
    public IVariableSymbol resolveClassConstant(ITSPHPAst ast) {
        return resolveClassMember(ast);
    }

    private ISymbol resolveInClassSymbol(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        ISymbol symbol;
        if (classTypeSymbol != null) {
            symbol = classTypeSymbol.resolveWithFallbackToParent(ast);
        } else {
            ReferenceException exception = ErrorReporterRegistry.get().notInClass(ast);
            symbol = symbolFactory.createErroneousAccessSymbol(ast, exception);
        }
        return symbol;
    }

    @Override
    public IVariableSymbol resolveClassMember(ITSPHPAst ast) {
        ISymbol symbol = resolveInClassSymbol(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public IMethodSymbol resolveMethod(ITSPHPAst callee, ITSPHPAst id) {
        IMethodSymbol methodSymbol;

        IVariableSymbol variableSymbol = (IVariableSymbol) callee.getSymbol();
        if (!(variableSymbol instanceof IErroneousSymbol)) {
            ITypeSymbol typeSymbol = variableSymbol.getType();
            if (!(typeSymbol instanceof IErroneousSymbol)) {
                methodSymbol = resolveMethod(typeSymbol, callee, id);
            } else {
                methodSymbol = symbolFactory.createErroneousMethodSymbol(id,
                        ((IErroneousSymbol) typeSymbol).getException());
            }
        } else {
            methodSymbol = symbolFactory.createErroneousMethodSymbol(id,
                    ((IErroneousSymbol) variableSymbol).getException());
        }
        if (methodSymbol == null) {
            DefinitionException exception = ErrorReporterRegistry.get().methodNotDefined(callee, id);
            methodSymbol = symbolFactory.createErroneousMethodSymbol(id, exception);
        }
        return methodSymbol;
    }

    private IMethodSymbol resolveMethod(ITypeSymbol typeSymbol, ITSPHPAst callee, ITSPHPAst id) {
        IMethodSymbol methodSymbol;
        if (typeSymbol instanceof IPolymorphicTypeSymbol) {
            IPolymorphicTypeSymbol inheritableTypeSymbol = (IPolymorphicTypeSymbol) typeSymbol;
            methodSymbol = (IMethodSymbol) inheritableTypeSymbol.resolveWithFallbackToParent(id);
        } else {
            DefinitionException exception = ErrorReporterRegistry.get().objectExpected(callee, typeSymbol.getDefinitionAst());
            methodSymbol = symbolFactory.createErroneousMethodSymbol(id, exception);
        }
        return methodSymbol;
    }

    @Override
    public IVariableSymbol resolveThisSelf(ITSPHPAst ast) {
        return resolveThis(getEnclosingClass(ast), ast);
    }

    private IVariableSymbol resolveThis(IClassTypeSymbol classTypeSymbol, ITSPHPAst $this) {
        IVariableSymbol variableSymbol;
        if (classTypeSymbol != null) {
            variableSymbol = classTypeSymbol.getThis();
            if (variableSymbol == null) {
                variableSymbol = symbolFactory.createThisSymbol($this, classTypeSymbol);
                classTypeSymbol.setThis(variableSymbol);
            }
        } else {
            ReferenceException exception = ErrorReporterRegistry.get().notInClass($this);
            variableSymbol = symbolFactory.createErroneousVariableSymbol($this, exception);
        }
        return variableSymbol;
    }

    @Override
    public IVariableSymbol resolveParent(ITSPHPAst ast) {
        return resolveThis(getParent(ast), ast);
    }

    @Override
    public IVariableSymbol resolveVariable(ITSPHPAst ast) {
        ISymbol symbol = ast.getScope().resolve(ast);
        if (symbol == null) {
            ReferenceException exception = ErrorReporterRegistry.get().notDefined(ast);
            symbol = symbolFactory.createErroneousVariableSymbol(ast, exception);
        }
        return (IVariableSymbol) symbol;
    }

    @Override
    public ITypeSymbol resolveUseType(ITSPHPAst typeAst, ITSPHPAst alias) {
        return resolver.resolveUseType(typeAst, alias);
    }

    @Override
    public ITypeSymbol resolveType(ITSPHPAst typeAst) {
        ITypeSymbol symbol = (ITypeSymbol) resolver.resolveGlobalIdentifier(typeAst);

        if (symbol == null) {
            String typeName = typeAst.getText();
            if (!resolver.isAbsolute(typeName)) {
                typeAst.setText(resolver.getEnclosingGlobalNamespaceScope(typeAst.getScope()).getScopeName() + typeName);
            }
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(typeAst, ex);

        } else if (symbol instanceof IAliasTypeSymbol) {

            typeAst.setText(symbol.getName());
            ReferenceException ex = ErrorReporterRegistry.get().unkownType(typeAst);
            symbol = symbolFactory.createErroneousTypeSymbol(symbol.getDefinitionAst(), ex);
        }
        return symbol;
    }

    @Override
    public ITypeSymbol resolvePrimitiveType(ITSPHPAst typeASt) {
        return (ITypeSymbol) globalDefaultNamespace.resolve(typeASt);
    }

    private IClassTypeSymbol getEnclosingClass(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = resolver.getEnclosingClass(ast);
        if (classTypeSymbol == null) {
            ReferenceException ex = ErrorReporterRegistry.get().notInClass(ast);
            classTypeSymbol = symbolFactory.createErroneousClassSymbol(ast, ex);
        }
        return classTypeSymbol;
    }

    private IClassTypeSymbol getParent(ITSPHPAst ast) {
        IClassTypeSymbol classTypeSymbol = getEnclosingClass(ast);
        IClassTypeSymbol parent = (IClassTypeSymbol) classTypeSymbol.getParent();
        if (parent == null) {
            TypeCheckerException ex = ErrorReporterRegistry.get().noParentClass(classTypeSymbol.getDefinitionAst());
            parent = symbolFactory.createErroneousClassSymbol(ast, ex);
        }
        return parent;
    }
}