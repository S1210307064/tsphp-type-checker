package ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck;

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
