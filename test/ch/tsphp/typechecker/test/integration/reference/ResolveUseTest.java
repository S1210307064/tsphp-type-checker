/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class ResolveUseTest from the TinsPHP project.
 * TinsPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceTypeScopeTest;
import ch.tsphp.typechecker.test.integration.testutils.reference.TypeScopeTestStruct;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(Parameterized.class)
public class ResolveUseTest extends AReferenceTypeScopeTest
{


    public ResolveUseTest(String testString, TypeScopeTestStruct[] theTestStructs) {
        super(testString, theTestStructs);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                //use from default namespace
                {
                        "namespace t{use \\A;} namespace{class A{}}",
                        typeStruct("A", "\\t\\.\\t\\.", "A", "\\.\\.", 0, 1, 0, 0, 1)
                },
                {
                        "namespace a\\b{const int a = 1;} namespace t{use \\a\\b; b\\a;}",
                        typeStruct("\\a\\b\\a#", "\\a\\b\\.\\a\\b\\.", "int", "\\.", 1, 1, 1, 0)
                }
        });
    }

    private static TypeScopeTestStruct[] typeStruct(
            String astText, String astScope, String typeText, String typeScope, Integer... astAccessOrder) {
        return new TypeScopeTestStruct[]{
                new TypeScopeTestStruct(astText, astScope, Arrays.asList(astAccessOrder), typeText, typeScope)
        };

    }
}
