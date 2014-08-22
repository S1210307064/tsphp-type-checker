/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.testutils;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.IDefinitionPhaseController;
import ch.tsphp.typechecker.test.integration.testutils.definition.TestTSPHPDefinitionWalker;
import org.antlr.runtime.tree.TreeNodeStream;
import org.junit.Ignore;

import static org.mockito.Mockito.mock;

@Ignore
public abstract class ADefinitionWalkerTest extends AWalkerTest
{
    protected TreeNodeStream treeNodeStream;
    protected IDefinitionPhaseController definitionPhaseController;

    protected TestTSPHPDefinitionWalker createWalker(ITSPHPAst ast) {
        treeNodeStream = createTreeNodeStream(ast);
        definitionPhaseController = mock(IDefinitionPhaseController.class);
        return new TestTSPHPDefinitionWalker(treeNodeStream, definitionPhaseController);
    }
}
