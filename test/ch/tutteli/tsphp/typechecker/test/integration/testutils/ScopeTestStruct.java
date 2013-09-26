package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import java.util.List;

public class ScopeTestStruct
{

    public String astText;
    public String astScope;
    public List<Integer> astAccessOrder;

    public ScopeTestStruct(String theAstText, String theAstScope, List<Integer> theAstAccessOrder) {
        astText = theAstText;
        astScope = theAstScope;
        astAccessOrder = theAstAccessOrder;
    }
}
