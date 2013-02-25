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

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.ITypeChecker;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tutteli.tsphp.typechecker.error.ErrorReporter;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.ScopeFactory;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelperRegistry;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.SymbolFactory;
import ch.tutteli.tsphp.typechecker.utils.AstHelper;
import ch.tutteli.tsphp.typechecker.utils.IAstHelper;
import java.util.List;
import org.antlr.runtime.tree.TreeNodeStream;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TypeChecker implements ITypeChecker
{

    private ITypeCheckerController controller;

    public TypeChecker(ITSPHPAstAdaptor astAdaptor) {
        ScopeHelperRegistry.set(new ScopeHelper());
        ErrorReporterRegistry.set(new ErrorReporter(new ErrorMessageProvider()));
        ISymbolFactory symbolFactory = new SymbolFactory();
        IAstHelper astHelper = new AstHelper(astAdaptor);
        IDefiner definer = new Definer(symbolFactory, new ScopeFactory());
        ISymbolTable symbolTable = new SymbolTable(symbolFactory, astHelper,
                definer.getGlobalDefaultNamespace());

        ISymbolResolver symbolResolver = new SymbolResolver(symbolFactory, definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());

        IOverloadResolver overloadResolver = new OverloadResolver(symbolTable);

        controller = new TypeCheckerController(
                symbolFactory,
                symbolTable,
                definer,
                symbolResolver,
                overloadResolver,
                astHelper);
    }

    @Override
    public void enrichWithDefinitions(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        TSPHPDefinitionWalker definition = new TSPHPDefinitionWalker(treeNodeStream, controller.getDefiner());
        definition.downup(ast);
    }

    @Override
    public void enrichWithReferences(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        treeNodeStream.reset();
        TSPHPReferenceWalker reference = new TSPHPReferenceWalker(treeNodeStream, controller);
        reference.downup(ast);
    }

    @Override
    public boolean hasFoundError() {
        return ErrorReporterRegistry.get().hasFoundError();
    }

    @Override
    public List<Exception> getExceptions() {
        return ErrorReporterRegistry.get().getExceptions();
    }
}
