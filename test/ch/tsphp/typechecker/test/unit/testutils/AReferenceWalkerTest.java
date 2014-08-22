/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.unit.testutils;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.test.integration.testutils.reference.TestTSPHPReferenceWalker;
import org.antlr.runtime.tree.TreeNodeStream;
import org.junit.Ignore;

import static org.mockito.Mockito.mock;

@Ignore
public abstract class AReferenceWalkerTest extends AWalkerTest
{
    protected TreeNodeStream treeNodeStream;
    protected IReferencePhaseController referencePhaseController;
    protected IAccessResolver accessResolver;

    protected TestTSPHPReferenceWalker createWalker(ITSPHPAst ast) {
        treeNodeStream = createTreeNodeStream(ast);
        referencePhaseController = mock(IReferencePhaseController.class);
        accessResolver = mock(IAccessResolver.class);
        return new TestTSPHPReferenceWalker(treeNodeStream, referencePhaseController, accessResolver);
    }
}
