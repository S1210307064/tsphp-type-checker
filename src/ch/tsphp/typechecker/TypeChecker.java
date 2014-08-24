/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker;

import ch.tsphp.common.AstHelperRegistry;
import ch.tsphp.common.IErrorLogger;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.ITypeChecker;
import ch.tsphp.common.exceptions.TSPHPException;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPDefinitionWalker;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPTypeCheckWalker;
import ch.tsphp.typechecker.error.HardCodedErrorMessageProvider;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.error.TypeCheckerErrorReporter;
import ch.tsphp.typechecker.scopes.IGlobalNamespaceScope;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.scopes.ScopeFactory;
import ch.tsphp.typechecker.scopes.ScopeHelper;
import ch.tsphp.typechecker.symbols.ISymbolFactory;
import ch.tsphp.typechecker.symbols.SymbolFactory;
import ch.tsphp.typechecker.utils.ITypeCheckerAstHelper;
import ch.tsphp.typechecker.utils.TypeCheckerAstHelper;
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
    private ITypeCheckerErrorReporter typeCheckErrorReporter;

    private final Collection<IErrorLogger> errorLoggers = new ArrayDeque<>();
    private boolean hasFoundError = false;

    public TypeChecker() {
        typeCheckErrorReporter = createTypeCheckerErrorReporter();
        init();
    }

    private void init() {
        IScopeHelper scopeHelper = createScopeHelper(typeCheckErrorReporter);
        ISymbolFactory symbolFactory = createSymbolFactory(scopeHelper);
        definitionPhaseController = createDefinitionPhaseController(scopeHelper, symbolFactory, typeCheckErrorReporter);
        IGlobalNamespaceScope globalDefaultNamespace = definitionPhaseController.getGlobalDefaultNamespace();

        typeSystem = createTypeSystem(symbolFactory, globalDefaultNamespace);

        ISymbolResolver symbolResolver = createSymbolResolver(
                scopeHelper, symbolFactory, typeCheckErrorReporter, globalDefaultNamespace);

        accessResolver = createAccessResolver(symbolFactory, typeCheckErrorReporter);

        referencePhaseController = createReferencePhaseController(
                symbolFactory, symbolResolver, typeCheckErrorReporter, globalDefaultNamespace);

        IOverloadResolver overloadResolver = createOverloadResolver(typeSystem);
        ITypeCheckerAstHelper typeCheckerAstHelper = createTypeCheckerAstHelper();

        typeCheckPhaseController = createTypeCheckPhaseController(
                symbolFactory,
                symbolResolver,
                typeCheckErrorReporter,
                typeSystem,
                overloadResolver,
                accessResolver,
                typeCheckerAstHelper);
    }

    protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
        return new TypeCheckerErrorReporter(new HardCodedErrorMessageProvider());
    }

    protected IScopeHelper createScopeHelper(ITypeCheckerErrorReporter typeCheckerErrorReporter) {
        return new ScopeHelper(typeCheckerErrorReporter);
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

    protected ISymbolResolver createSymbolResolver(
            IScopeHelper scopeHelper,
            ISymbolFactory symbolFactory,
            ITypeCheckerErrorReporter theTypeCheckErrorReporter,
            IGlobalNamespaceScope globalDefaultNamespace) {
        return new SymbolResolver(
                scopeHelper,
                symbolFactory,
                theTypeCheckErrorReporter,
                definitionPhaseController.getGlobalNamespaceScopes(),
                globalDefaultNamespace
        );
    }

    protected IAccessResolver createAccessResolver(
            ISymbolFactory symbolFactory, ITypeCheckerErrorReporter theTypeCheckErrorReporter) {
        return new AccessResolver(symbolFactory, theTypeCheckErrorReporter);
    }

    protected IReferencePhaseController createReferencePhaseController(
            ISymbolFactory symbolFactory,
            ISymbolResolver symbolResolver,
            ITypeCheckerErrorReporter theTypeCheckErrorReporter,
            IGlobalNamespaceScope globalDefaultNamespace) {
        return new ReferencePhaseController(
                symbolFactory,
                symbolResolver,
                theTypeCheckErrorReporter,
                globalDefaultNamespace);
    }

    protected IOverloadResolver createOverloadResolver(ITypeSystem theTypeSystem) {
        return new OverloadResolver(theTypeSystem);
    }

    protected ITypeCheckerAstHelper createTypeCheckerAstHelper() {
        return new TypeCheckerAstHelper();
    }

    protected ITypeCheckPhaseController createTypeCheckPhaseController(
            ISymbolFactory symbolFactory,
            ISymbolResolver symbolResolver,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter,
            ITypeSystem theTypeSystem,
            IOverloadResolver overloadResolver,
            IAccessResolver theAccessResolver,
            ITypeCheckerAstHelper astHelper) {
        return new TypeCheckPhaseController(
                symbolFactory,
                symbolResolver,
                theTypeCheckerErrorReporter,
                theTypeSystem,
                overloadResolver,
                theAccessResolver,
                astHelper);
    }

    protected IDefinitionPhaseController createDefinitionPhaseController(
            IScopeHelper scopeHelper,
            ISymbolFactory symbolFactory,
            ITypeCheckerErrorReporter theTypeCheckerErrorReporter) {
        return new DefinitionPhaseController(symbolFactory, new ScopeFactory(scopeHelper, theTypeCheckerErrorReporter));
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
        return hasFoundError || typeCheckErrorReporter.hasFoundError();
    }

    @Override
    public void registerErrorLogger(IErrorLogger errorLogger) {
        errorLoggers.add(errorLogger);
        typeCheckErrorReporter.registerErrorLogger(errorLogger);
    }

    @Override
    public void reset() {
        hasFoundError = false;
        init();
        typeCheckErrorReporter.reset();
    }

    @Override
    public void log(TSPHPException exception) {
        hasFoundError = true;
    }
}
