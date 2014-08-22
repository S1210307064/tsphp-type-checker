/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.typechecker.IAccessResolver;
import ch.tsphp.typechecker.IReferencePhaseController;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPReferenceWalker;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.TreeNodeStream;

public class TestTSPHPReferenceWalker extends ErrorReportingTSPHPReferenceWalker
{

    public TestTSPHPReferenceWalker(TreeNodeStream input, IReferencePhaseController controller,
            IAccessResolver accessResolver) {
        super(input, controller, accessResolver);
    }

    public RecognizerSharedState getState() {
        return state;
    }
}
