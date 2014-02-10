package ch.tsphp.typechecker.symbols.erroneous;

import ch.tsphp.common.ASymbol;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.exceptions.TSPHPException;

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
