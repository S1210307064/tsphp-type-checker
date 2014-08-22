/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.definition;

import ch.tsphp.typechecker.IDefinitionPhaseController;
import ch.tsphp.typechecker.antlrmod.ErrorReportingTSPHPDefinitionWalker;
import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.TreeNodeStream;

public class TestTSPHPDefinitionWalker extends ErrorReportingTSPHPDefinitionWalker
{


    public TestTSPHPDefinitionWalker(TreeNodeStream input, IDefinitionPhaseController theDefiner) {
        super(input, theDefiner);
    }

    public RecognizerSharedState getState() {
        return state;
    }
}
