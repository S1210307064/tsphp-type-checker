/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.typecheck;

import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.TypeScopeTestStruct;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.AReferenceScopeTypeCheckTest;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.EBuiltInType;
import ch.tsphp.typechecker.test.integration.testutils.typecheck.TypeCheckStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class FunctionCallTest extends AReferenceScopeTypeCheckTest
{

    public FunctionCallTest(String testString,
            TypeScopeTestStruct[] scopeTestStructs, TypeCheckStruct[] typeCheckStructs) {
        super(testString, scopeTestStructs, typeCheckStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        Object[][] types = TypeHelper.getAllTypesInclTokenAndDefaultValue();

        for (Object[] type : types) {
            String stat = type[0] + " $a=" + type[2] + "; return $a;";
            if (type[0].equals("void")) {
                stat = "";
            }

            String returnTypeString = (String) type[0];
            if (returnTypeString.charAt(0) == '\\') {
                returnTypeString = returnTypeString.substring(1);
            }
            EBuiltInType returnType = (EBuiltInType) type[1];
            collection.addAll(Arrays.asList(new Object[][]{
                    {
                            "function " + type[0] + " foo(){" + stat + "} foo();",
                            scopeStructDefault(returnTypeString, "", 1, 1, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0)
                    },
                    {
                            "namespace{function " + type[0] + " foo(){" + stat + "} foo();}",
                            scopeStructDefault(returnTypeString, "", 1, 1, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0)
                    },
                    {
                            "namespace{function " + type[0] + " foo(){" + stat + "}} namespace{ foo();}",
                            scopeStructDefault(returnTypeString, "", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    },
                    {
                            "namespace{ foo();} namespace{function " + type[0] + " foo(){" + stat + "}} ",
                            scopeStructDefault(returnTypeString, "", 0, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 0, 1, 0, 0)
                    },
                    {
                            "namespace a{function " + type[0] + " foo(){" + stat + "}} namespace a{ foo();}",
                            scopeStruct(returnTypeString, "", "\\a\\.\\a\\.", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    },
                    {
                            "namespace a{ foo();} namespace a{function " + type[0] + " foo(){" + stat + "}} ",
                            scopeStruct(returnTypeString, "", "\\a\\.\\a\\.", 0, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 0, 1, 0, 0)
                    },
                    //absolute path
                    {
                            "namespace{function " + type[0] + " foo(){" + stat + "}} namespace a{ \\foo();}",
                            scopeStructDefault(returnTypeString, "\\", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    },
                    {
                            "namespace a\\b{function " + type[0] + " foo(){" + stat + "}} namespace x{ \\a\\b\\foo();}",
                            scopeStruct(returnTypeString, "\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    },
                    //relative
                    {
                            "namespace a\\b{function " + type[0] + " foo(){" + stat + "}} namespace a{ b\\foo();}",
                            scopeStruct(returnTypeString, "\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    },
                    {
                            "namespace a\\b{function " + type[0] + " foo(){" + stat + "}} namespace { a\\b\\foo();}",
                            scopeStruct(returnTypeString, "\\a\\b\\", "\\a\\b\\.\\a\\b\\.", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    },
                    //using an alias
                    {
                            "namespace a{function " + type[0] + " foo(){" + stat + "}} "
                                    + "namespace a\\a\\c{ use a as b; b\\foo();}",
                            scopeStruct(returnTypeString, "\\a\\", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 1, 0)
                    },
                    {
                            "namespace a{function " + type[0] + " foo(){" + stat + "}} "
                                    + "namespace a\\a\\c{ use a as b; b\\foo();}",
                            scopeStruct(returnTypeString, "\\a\\", "\\a\\.\\a\\.", 1, 1, 1, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 1, 0)
                    },
                    //fallback to global
                    {
                            "namespace{function " + type[0] + " foo(){" + stat + "}} namespace a{ foo();}",
                            scopeStruct(returnTypeString, "", "\\.\\.", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0),},
                    {
                            "namespace{function " + type[0] + " foo(){" + stat + "}} namespace a\\b{ foo();}",
                            scopeStruct(returnTypeString, "", "\\.\\.", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    },
                    {
                            "namespace{function " + type[0] + " foo(){" + stat + "}} namespace a\\a\\c{ foo();}",
                            scopeStruct(returnTypeString, "", "\\.\\.", 1, 1, 0, 0, 0),
                            typeStruct("fCall", returnType, 1, 1, 0, 0)
                    }
            }));
        }
        return collection;
    }

    private static TypeScopeTestStruct[] scopeStructDefault(String type, String prefix, Integer... accessToScope) {
        return scopeStruct(type, prefix, "\\.\\.", accessToScope);
    }

    private static TypeScopeTestStruct[] scopeStruct(String type, String prefix, String scope,
            Integer... accessToScope) {
        return new TypeScopeTestStruct[]{
                new TypeScopeTestStruct(prefix + "foo()", scope, Arrays.asList(accessToScope), type, "\\.")
        };
    }
}
