package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;
import java.util.Set;

public class ErroneousVariableSymbol extends AErroneousSymbol implements IErroneousVariableSymbol
{

    public ErroneousVariableSymbol(ITSPHPAst ast, TypeCheckerException exception) {
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
        throw new UnsupportedOperationException("ErroneousVariableSymbol is not a real symbol with modifier.");
    }

    @Override
    public boolean removeModifier(Integer modifier) {
        throw new UnsupportedOperationException("ErroneousVariableSymbol is not a real symbol with modifier.");
    }

    @Override
    public Set<Integer> getModifiers() {
        throw new UnsupportedOperationException("ErroneousVariableSymbol is not a real symbol with modifier.");
    }

    @Override
    public void setModifiers(Set<Integer> modifier) {
        throw new UnsupportedOperationException("ErroneousVariableSymbol is not a real symbol with modifier.");
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
