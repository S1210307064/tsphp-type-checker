/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class VariableOutsideConditionalScopeErrorTest extends AReferenceDefinitionErrorTest
{

    public VariableOutsideConditionalScopeErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        //global variables
        collection.addAll(getVariations("", "", "int\n $a=1;", "\n $a;"));
        collection.addAll(getVariations("namespace a;", "", "int\n $a=0;", "\n $a;"));
        collection.addAll(getVariations("namespace a\\b;", "", "int\n $a=2;", "\n $a;"));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));

        //functions
        collection.addAll(getVariations("function void foo(){", "}", "int\n $a=0;", "\n $a;"));
        collection.addAll(getVariations("namespace a;function void foo(){", "}", "int\n $a=0;", "\n $a;"));
        collection.addAll(getVariations("namespace a\\b;function void foo(){", "}", "int\n $a=0;", "\n $a;"));
        collection.addAll(getVariations("namespace{function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a{function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a\\b\\z{function void foo(){", "}}"));

        //methods
        collection.addAll(getVariations("class a{ function void foo(){", "}}", "int\n $a=0;", "\n $a;"));
        collection.addAll(getVariations("namespace a;class a{ function void foo(){", "}}", "int\n $a=0;", "\n $a;"));
        collection.addAll(getVariations("namespace a\\b;class a{ function void foo(){", "}}", "int\n $a=0;", "\n $a;"));
        collection.addAll(getVariations("namespace{class a{ function void foo(){", "}}}"));
        collection.addAll(getVariations("namespace a{class a{ function void foo(){", "}}}"));
        collection.addAll(getVariations("namespace a\\b\\z{class a{ function void foo(){", "}}}"));

        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getVariations(prefix, appendix, "int\n $a=1;", "\n $a;"));

        //ensure check is also done when accessing a field
        collection.addAll(getVariations(
                "namespace z{class Z{public int $foo;}}" + prefix, appendix, "\\z\\Z \n $a=null;", "\n $a->foo;"));

        //ensure check is also done when calling a method - see TSPHP-655
        collection.addAll(getVariations("namespace z{class Z{public function void foo(){}}}" + prefix, appendix,
                "\\z\\Z \n $a=null;", "\n $a->foo();"));

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1)};
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "foreach([1,2] as mixed\n $a); \n $a; " + appendix, errorDto},
                {prefix + "foreach([1,2] as string\n $a => mixed $v); \n $a; " + appendix, errorDto}
        }));

        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix,
            String declaration, String statement) {
        List<Object[]> collection = new ArrayList<>();

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1)};
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "if(true){" + declaration + "}" + statement + "" + appendix, errorDto},
                {prefix + "if(true);else " + declaration + "" + statement + "" + appendix, errorDto},
                {prefix + "if(true){}else{ " + declaration + "} " + statement + " " + appendix, errorDto},
                {prefix + "int $b=1; switch($b){case 1: " + declaration + "} " + statement + "" + appendix, errorDto},
                {prefix + "for(;;)" + declaration + " " + statement + " " + appendix, errorDto},
                {prefix + "for(;;){" + declaration + "} " + statement + " " + appendix, errorDto},
                {prefix + "foreach([1,2] as mixed $b){" + declaration + "} " + statement + " " + appendix, errorDto},
                {prefix + "while(true)" + declaration + " " + statement + "" + appendix, errorDto},
                {prefix + "if(true){ do{" + declaration + "}while(true);} " + statement + "" + appendix, errorDto},
                {prefix + "try{ " + declaration + "}catch(\\Exception $e){" + statement + "}" + appendix, errorDto},
                {
                        prefix + "try{}catch(\\ErrorException $e){" + declaration + "}"
                                + "catch(\\Exception $e2){" + statement + "}" + appendix,
                        errorDto
                },
                {
                        prefix + "if(true){ " + declaration + "}while(true);" + statement
                                + " if(true){" + statement + "}" + appendix,
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1),
                                new DefinitionErrorDto("$a", 2, 1, "$a", 4, 1)
                        }
                },
                {
                        prefix + "while(true){ " + "if(true){" + declaration + "} if(true){" + statement + "}" +
                                statement + "} " + appendix,
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1),
                                new DefinitionErrorDto("$a", 2, 1, "$a", 4, 1)
                        }
                }
        }));
        return collection;
    }
}
