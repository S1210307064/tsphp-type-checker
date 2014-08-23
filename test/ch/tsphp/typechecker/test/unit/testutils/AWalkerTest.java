/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.testutils;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.TSPHPAst;
import ch.tsphp.common.TSPHPAstAdaptor;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;
import org.junit.Ignore;

import static ch.tsphp.typechecker.antlr.TSPHPTypeCheckWalker.VariableId;
import static org.mockito.Mockito.mock;

@Ignore
public abstract class AWalkerTest
{

    protected TreeNodeStream createTreeNodeStream(ITSPHPAst ast) {
        return new CommonTreeNodeStream(new TSPHPAstAdaptor(), ast);
    }

    protected ITSPHPAst createAst(int tokenType) {
        return new TSPHPAst(new CommonToken(tokenType));
    }

    protected ITSPHPAst createVariable() {
        ITSPHPAst variable = createAst(VariableId);
        variable.setSymbol(mock(IVariableSymbol.class));
        return variable;
    }
}
