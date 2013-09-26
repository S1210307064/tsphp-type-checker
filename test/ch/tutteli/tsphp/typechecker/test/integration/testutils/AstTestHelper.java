package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.common.IScope;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.TSPHPAst;
import org.antlr.runtime.CommonToken;

public class AstTestHelper
{

    private AstTestHelper() {
    }

    public static ITSPHPAst getAstWithTokenText(String text, IScope scope) {
        ITSPHPAst ast = getAstWithTokenText(text);
        ast.setScope(scope);
        return ast;
    }

    public static ITSPHPAst getAstWithTokenText(String text) {
        return new TSPHPAst(new CommonToken(0, text));
    }
}
