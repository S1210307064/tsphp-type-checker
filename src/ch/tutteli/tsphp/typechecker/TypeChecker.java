package ch.tutteli.tsphp.typechecker;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.IErrorLogger;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeChecker;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPDefinitionWalker;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import ch.tutteli.tsphp.typechecker.antlrmod.ErrorReportingTSPHPTypeCheckWalker;
import ch.tutteli.tsphp.typechecker.error.ErrorMessageProvider;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.TypeCheckErrorReporterRegistry;
import ch.tutteli.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tutteli.tsphp.typechecker.scopes.IScopeHelper;
import ch.tutteli.tsphp.typechecker.scopes.ScopeFactory;
import ch.tutteli.tsphp.typechecker.scopes.ScopeHelper;
import ch.tutteli.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tutteli.tsphp.typechecker.symbols.SymbolFactory;
import ch.tutteli.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import ch.tutteli.tsphp.typechecker.utils.TypeCheckerAstHelper;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.TreeNodeStream;

import java.util.ArrayDeque;
import java.util.Collection;

public class TypeChecker implements ITypeChecker, IErrorLogger
{

    private IDefinitionPhaseController definitionPhaseController;
    private IReferencePhaseController referencePhaseController;
    private ITypeCheckPhaseController typeCheckPhaseController;
    private IAccessResolver accessResolver;
    private ITypeSystem typeSystem;

    private final Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError = false;

    public TypeChecker() {
        TypeCheckErrorReporterRegistry.set(new TypeCheckErrorReporter(new ErrorMessageProvider()));
    }

    private void init() {
        IScopeHelper scopeHelper = createScopeHelper();
        ISymbolFactory symbolFactory = createSymbolFactory(scopeHelper);
        definitionPhaseController = createDefinitionPhaseController(scopeHelper, symbolFactory);
        IGlobalNamespaceScope globalDefaultNamespace = definitionPhaseController.getGlobalDefaultNamespace();

        typeSystem = createTypeSystem(symbolFactory, globalDefaultNamespace);

        ISymbolResolver symbolResolver = createSymbolResolver(scopeHelper, symbolFactory, globalDefaultNamespace);
        accessResolver = createAccessResolver(symbolFactory);

        referencePhaseController = createReferencePhaseController(
                symbolFactory, symbolResolver, globalDefaultNamespace);

        IOverloadResolver overloadResolver = createOverloadResolver(typeSystem);
        ITypeCheckerAstHelper typeCheckerAstHelper = createTypeCheckerAstHelper();

        typeCheckPhaseController = createTypeCheckPhaseController(
                symbolFactory, typeSystem, symbolResolver, overloadResolver, accessResolver, typeCheckerAstHelper);
    }

    protected IScopeHelper createScopeHelper() {
        return new ScopeHelper();
    }

    protected ISymbolFactory createSymbolFactory(IScopeHelper scopeHelper) {
        return new SymbolFactory(scopeHelper);
    }

    protected ITypeSystem createTypeSystem(ISymbolFactory symbolFactory, IGlobalNamespaceScope globalDefaultNamespace) {
        return new TypeSystem(
                symbolFactory,
                AstHelperRegistry.get(),
                globalDefaultNamespace);
    }

    protected ISymbolResolver createSymbolResolver(IScopeHelper scopeHelper, ISymbolFactory symbolFactory,
            IGlobalNamespaceScope globalDefaultNamespace) {
        return new SymbolResolver(
                scopeHelper,
                symbolFactory,
                definitionPhaseController.getGlobalNamespaceScopes(),
                globalDefaultNamespace
        );
    }

    protected IAccessResolver createAccessResolver(ISymbolFactory symbolFactory) {
        return new AccessResolver(symbolFactory);
    }

    protected IReferencePhaseController createReferencePhaseController(
            ISymbolFactory symbolFactory,
            ISymbolResolver symbolResolver,
            IGlobalNamespaceScope globalDefaultNamespace) {
        return new ReferencePhaseController(
                symbolFactory,
                symbolResolver,
                globalDefaultNamespace);
    }

    protected IOverloadResolver createOverloadResolver(ITypeSystem typeSystem) {
        return new OverloadResolver(typeSystem);
    }

    protected ITypeCheckerAstHelper createTypeCheckerAstHelper() {
        return new TypeCheckerAstHelper();
    }


    protected ITypeCheckPhaseController createTypeCheckPhaseController(
            ISymbolFactory symbolFactory,
            ITypeSystem typeSystem,
            ISymbolResolver symbolResolver,
            IOverloadResolver overloadResolver,
            IAccessResolver accessResolver,
            ITypeCheckerAstHelper astHelper) {
        return new TypeCheckPhaseController(
                symbolFactory,
                symbolResolver, typeSystem,
                overloadResolver,
                accessResolver,
                astHelper);
    }

    protected IDefinitionPhaseController createDefinitionPhaseController(IScopeHelper scopeHelper, ISymbolFactory
            symbolFactory) {
        return new DefinitionPhaseController(symbolFactory, new ScopeFactory(scopeHelper));
    }

    @Override
    public void enrichWithDefinitions(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        ErrorReportingTSPHPDefinitionWalker definition =
                new ErrorReportingTSPHPDefinitionWalker(treeNodeStream, definitionPhaseController);
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
                new ErrorReportingTSPHPReferenceWalker(treeNodeStream, referencePhaseController, accessResolver);
        for (IErrorLogger logger : errorLoggers) {
            reference.registerErrorLogger(logger);
        }
        reference.registerErrorLogger(this);
        try {
            reference.compilationUnit();
        } catch (RecognitionException ex) {
            // should never happen, ErrorReportingTSPHPReferenceWalker should catch it already.
            // but just in case and to be complete
            hasFoundError = true;
            for (IErrorLogger logger : errorLoggers) {
                logger.log(new TSPHPException(ex));
            }
        }
    }


    @Override
    public void doTypeChecking(ITSPHPAst ast, TreeNodeStream treeNodeStream) {
        treeNodeStream.reset();

        ErrorReportingTSPHPTypeCheckWalker typeCheckWalker = new ErrorReportingTSPHPTypeCheckWalker(
                        treeNodeStream, typeCheckPhaseController, accessResolver, typeSystem);

        for (IErrorLogger logger : errorLoggers) {
            typeCheckWalker.registerErrorLogger(logger);
        }
        typeCheckWalker.registerErrorLogger(this);
        typeCheckWalker.downup(ast);
    }

    @Override
    public boolean hasFoundError() {
        return hasFoundError || TypeCheckErrorReporterRegistry.get().hasFoundError();
    }

    @Override
    public void registerErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
        TypeCheckErrorReporterRegistry.get().registerErrorLogger(errorLogger);
    }

    @Override
    public void reset() {
        hasFoundError = false;
        init();

        TypeCheckErrorReporterRegistry.get().reset();
    }

    @Override
    public void log(TSPHPException exception) {
        hasFoundError = true;
    }
}
