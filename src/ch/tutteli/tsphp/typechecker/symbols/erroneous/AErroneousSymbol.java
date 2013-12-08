package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TSPHPException;

public abstract class AErroneousSymbol extends ASymbol implements IErroneousSymbol
{

    private final TSPHPException exception;

    public AErroneousSymbol(ITSPHPAst ast, TSPHPException theException) {
        super(ast, ast.getText());
        exception = theException;
    }

    @Override
    public TSPHPException getException() {
        return exception;
    }
}
