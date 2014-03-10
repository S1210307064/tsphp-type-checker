/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.symbols.erroneous;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;

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
