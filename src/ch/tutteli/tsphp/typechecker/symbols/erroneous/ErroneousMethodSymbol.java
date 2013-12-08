package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;
import ch.tutteli.tsphp.typechecker.symbols.IVariableSymbol;

import java.util.List;

public class ErroneousMethodSymbol extends AErroneousScopedSymbol implements IErroneousMethodSymbol
{

    public static final String ERROR_MESSAGE = "ErroneousMethodSymbol is not a real method.";

    public ErroneousMethodSymbol(ITSPHPAst ast, TSPHPException exception) {
        super(ast, exception);
    }

    @Override
    public void addParameter(IVariableSymbol variableSymbol) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public List<IVariableSymbol> getParameters() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isAbstract() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return true;
    }

    @Override
    public boolean isAlwaysCasting() {
        return true;
    }

    @Override
    public boolean canBeAccessedFrom(int type) {
        return true;
    }

    @Override
    public boolean isPublic() {
        return true;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isPrivate() {
        return false;
    }
}
