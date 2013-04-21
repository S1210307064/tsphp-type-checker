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

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITSPHPAstAdaptor;
import ch.tutteli.tsphp.common.ITypeChecker;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPTypeCheckWalker;
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
import java.util.ArrayDeque;
import java.util.Collection;
import org.antlr.runtime.tree.TreeNodeStream;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class TypeChecker implements ITypeChecker, IErrorLogger
{

    private ITypeCheckerController controller;
    private Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError = false;

    public TypeChecker() {
        ScopeHelperRegistry.set(new ScopeHelper());
        ErrorReporterRegistry.set(new ErrorReporter(new ErrorMessageProvider()));

        init();
    }

    private void init() {
        ISymbolFactory symbolFactory = new SymbolFactory();
        IDefiner definer = new Definer(symbolFactory, new ScopeFactory());

        ITypeSystem typeSystem = new TypeSystem(symbolFactory, AstHelperRegistry.get(),
                definer.getGlobalDefaultNamespace());

        ISymbolResolver symbolResolver = new SymbolResolver(symbolFactory, definer.getGlobalNamespaceScopes(),
                definer.getGlobalDefaultNamespace());

        IOverloadResolver overloadResolver = new OverloadResolver(typeSystem);

        controller = new TypeCheckerController(
                symbolFactory,
                typeSystem,
                definer,
                symbolResolver,
                overloadResolver,
                new AstHelper());
    }

    @Override
    public void enrichWithDefinitions(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        ErrorReportingTSPHPDefinitionWalker definition =
                new ErrorReportingTSPHPDefinitionWalker(treeNodeStream, controller.getDefiner());
        for (IErrorLogger logger : errorLoggers) {
            definition.addErrorLogger(logger);
        }
        definition.addErrorLogger(this);
        definition.downup(ast);
    }

    @Override
    public void enrichWithReferences(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        treeNodeStream.reset();
        ErrorReportingTSPHPReferenceWalker reference =
                new ErrorReportingTSPHPReferenceWalker(treeNodeStream, controller);
        for (IErrorLogger logger : errorLoggers) {
            reference.addErrorLogger(logger);
        }
        reference.addErrorLogger(this);
        reference.downup(ast);
    }

    @Override
    public boolean hasFoundError() {
        return hasFoundError || ErrorReporterRegistry.get().hasFoundError();
    }

    @Override
    public void addErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
        ErrorReporterRegistry.get().addErrorLogger(errorLogger);
    }

    @Override
    public void doTypeChecking(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        treeNodeStream.reset();
        ErrorReportingTSPHPTypeCheckWalker typeCheckWalker =
                new ErrorReportingTSPHPTypeCheckWalker(treeNodeStream, controller);
        for (IErrorLogger logger : errorLoggers) {
            typeCheckWalker.addErrorLogger(logger);
        }
        typeCheckWalker.addErrorLogger(this);
        typeCheckWalker.downup(ast);
    }

    @Override
    public void reset() {
        hasFoundError = false;
        init();

        ErrorReporterRegistry.get().reset();
    }

    @Override
    public void log(TSPHPException tsphpe) {
        hasFoundError = true;
    }
}
