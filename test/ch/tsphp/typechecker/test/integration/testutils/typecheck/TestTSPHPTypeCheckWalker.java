/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.ITypeCheckPhaseController;
import ch.tsphp.typechecker.ITypeSystem;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPTypeCheckWalker;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.TreeNodeStream;

public class TestTSPHPTypeCheckWalker extends ErrorReportingTSPHPTypeCheckWalker
{
    public TestTSPHPTypeCheckWalker(TreeNodeStream input, ITypeCheckPhaseController controller,
            IAccessResolver accessResolver, ITypeSystem typeSystem) {
        super(input, controller, accessResolver, typeSystem);
    }

    public RecognizerSharedState getState() {
        return state;
    }
}
