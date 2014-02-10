package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;

import java.util.List;

public class ReferenceScopeTestStruct extends ScopeTestStruct
{

    public String typeText;
    public String typeScope;

    public ReferenceScopeTestStruct(String theAstText, String theAstScope, List<Integer> theAstAccessOrder,
            String theTypeText, String theTypeScope) {
        super(theAstText, theAstScope, theAstAccessOrder);
        typeText = theTypeText;
        typeScope = theTypeScope;

    }
}
