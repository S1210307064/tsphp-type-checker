/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.typecheck;

import java.util.List;

public class TypeCheckStruct
{

    public String astText;
    public EBuiltInType evalType;
    public List<Integer> accessOrderToNode;

    public TypeCheckStruct(String theAstText, EBuiltInType theEvalType, List<Integer> theAccessOrderToNode) {
        astText = theAstText;
        accessOrderToNode = theAccessOrderToNode;
        evalType = theEvalType;
    }
}
