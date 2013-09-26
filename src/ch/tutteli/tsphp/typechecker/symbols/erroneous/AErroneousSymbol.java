package ch.tutteli.tsphp.typechecker.symbols.erroneous;

import ch.tutteli.tsphp.common.ASymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.exceptions.TypeCheckerException;

public abstract class AErroneousSymbol extends ASymbol implements IErroneousSymbol
{

    private TypeCheckerException exception;

    public AErroneousSymbol(ITSPHPAst ast, TypeCheckerException theException) {
        super(ast, ast.getText());
    }
    
    @Override
    public TypeCheckerException getException() {
        return exception;
    }
}
