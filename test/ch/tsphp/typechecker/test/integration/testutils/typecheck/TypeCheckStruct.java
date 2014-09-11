/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;

import java.util.List;

public class TypeCheckStruct extends ScopeTestStruct
{

    public EBuiltInType evalType;

    public TypeCheckStruct(String theAstText, EBuiltInType theEvalType, List<Integer> theAccessOrderToNode) {
        super(theAstText, "", theAccessOrderToNode);
        evalType = theEvalType;
    }
}
