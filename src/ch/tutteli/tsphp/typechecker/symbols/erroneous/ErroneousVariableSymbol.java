package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;

import java.util.Set;

public class ErroneousVariableSymbol extends AErroneousSymbol implements IErroneousVariableSymbol
{

    public static final String ERROR_MESSAGE = "ErroneousVariableSymbol is not a real variable symbol.";

    public ErroneousVariableSymbol(ITSPHPAst ast, TSPHPException exception) {
        super(ast, exception);
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
    public void addModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Set<Integer> getModifiers() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public void setModifiers(Set<Integer> modifier) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
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
