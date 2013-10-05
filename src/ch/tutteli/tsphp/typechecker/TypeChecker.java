package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeChecker;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.antlr.ErrorReportingTSPHPTypeCheckWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tutteli.tsphp.typechecker.error.ErrorReporter;
import ch.tutteli.tsphp.typechecker.error.ErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeFactory;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelper;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.SymbolFactory;
import ch.tutteli.tsphp.typechecker.utils.AstHelper;
import org.antlr.runtime.tree.TreeNodeStream;

import java.util.ArrayDeque;
import java.util.Collection;

public class TypeChecker implements ITypeChecker, IErrorLogger
{

    private ITypeCheckerController controller;
    private Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError = false;

    public TypeChecker() {
        ErrorReporterRegistry.set(new ErrorReporter(new ErrorMessageProvider()));

        init();
    }

    private void init() {
        IScopeHelper scopeHelper = new ScopeHelper();
        ISymbolFactory symbolFactory = new SymbolFactory(scopeHelper);
        IDefiner definer = new Definer(symbolFactory, new ScopeFactory(scopeHelper));

        ITypeSystem typeSystem = new TypeSystem(
                symbolFactory,
                AstHelperRegistry.get(),
                definer.getGlobalDefaultNamespace());

        ISymbolResolver symbolResolver = new SymbolResolver(
                scopeHelper,
                symbolFactory,
                definer.getGlobalNamespaceScopes(),
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
            definition.registerErrorLogger(logger);
        }
        definition.registerErrorLogger(this);
        definition.downup(ast);
    }

    @Override
    public void enrichWithReferences(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        treeNodeStream.reset();
        ErrorReportingTSPHPReferenceWalker reference =
                new ErrorReportingTSPHPReferenceWalker(treeNodeStream, controller);
        for (IErrorLogger logger : errorLoggers) {
            reference.registerErrorLogger(logger);
        }
        reference.registerErrorLogger(this);
        reference.downup(ast);
    }

    @Override
    public boolean hasFoundError() {
        return hasFoundError || ErrorReporterRegistry.get().hasFoundError();
    }

    @Override
    public void registerErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
        ErrorReporterRegistry.get().registerErrorLogger(errorLogger);
    }

    @Override
    public void doTypeChecking(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        treeNodeStream.reset();
        ErrorReportingTSPHPTypeCheckWalker typeCheckWalker =
                new ErrorReportingTSPHPTypeCheckWalker(treeNodeStream, controller);
        for (IErrorLogger logger : errorLoggers) {
            typeCheckWalker.registerErrorLogger(logger);
        }
        typeCheckWalker.registerErrorLogger(this);
        typeCheckWalker.downup(ast);
    }

    @Override
    public void reset() {
        hasFoundError = false;
        init();

        ErrorReporterRegistry.get().reset();
    }

    @Override
    public void log(TSPHPException exception) {
        hasFoundError = true;
    }
}
