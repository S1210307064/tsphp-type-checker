package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;

public class ErroneousAccessSymbol extends AErroneousSymbol implements IErroneousAccessSymbol
{

    public ErroneousAccessSymbol(ITSPHPAst ast, TypeCheckerException exception) {
        super(ast, exception);
    }
}
