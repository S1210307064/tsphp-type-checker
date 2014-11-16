/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils.reference;

import ch.tsphp.typechecker.test.integration.testutils.ScopeTestStruct;

import java.util.List;

public class TypeScopeTestStruct extends ScopeTestStruct
{

    public String typeText;
    public String typeScope;

    public TypeScopeTestStruct(String theAstText, String theAstScope, List<Integer> theAstAccessOrder,
            String theTypeText, String theTypeScope) {
        super(theAstText, theAstScope, theAstAccessOrder);
        typeText = theTypeText;
        typeScope = theTypeScope;

    }
}
