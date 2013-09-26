package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.AstHelperRegistry;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import ch.tutteli.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import java.util.Set;

public class ErroneousTypeSymbol extends AErroneousSymbol implements IErroneousTypeSymbol
{

    public ErroneousTypeSymbol(ITSPHPAst type, TypeCheckerException exception) {
        super(type, exception);
    }

    @Override
    public Set<ITypeSymbol> getParentTypeSymbols() {
        throw new UnsupportedOperationException("ErroneousTypeSymbol is not a real type.");
    }

    @Override
    public boolean isNullable() {
        return true;
    }

    @Override
    public ITSPHPAst getDefaultValue() {
        return AstHelperRegistry.get().createAst(TSPHPDefinitionWalker.Null, "null");
    }
}
