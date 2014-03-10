/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

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
