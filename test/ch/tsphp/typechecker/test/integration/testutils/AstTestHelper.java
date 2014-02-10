package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.TSPHPAst;
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
