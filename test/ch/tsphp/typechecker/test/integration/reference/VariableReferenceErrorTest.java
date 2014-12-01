/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class VariableReferenceErrorTest extends AReferenceErrorTest
{

    private static List<Object[]> collection;

    public VariableReferenceErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        //global
        addVariations("", "");
        addVariations("namespace a;", "");
        addVariations("namespace a\\b\\z{", "}");

        //functions
        addVariations("function void foo(){", "}");
        addVariations("namespace a;function void foo(){", "}");
        addVariations("namespace a\\b\\z{function void foo(){", "}}");

        //methods
        addVariations("class a{ function void foo(){", "}}");
        addVariations("namespace a; class a{ function void foo(){", "}}");
        addVariations("namespace a\\b\\z{ class a{ function void foo(){", "}}}");

        return collection;
    }

    private static void addVariations(String prefix, String appendix) {

        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("$a", 2, 1)};

        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "\n $a;" + appendix, errorDto},
                {prefix + "{\n $a=2;}" + appendix, errorDto},
                {prefix + "if(\n $a==1){}" + appendix, errorDto},
                {prefix + "if(true){\n $a=2;}" + appendix, errorDto},
                {prefix + "if(true){}else{\n $a=2;}" + appendix, errorDto},
                {prefix + "if(true){ if(true){\n $a=2;}}" + appendix, errorDto},
                {prefix + "switch(\n $a){}" + appendix, errorDto},
                {prefix + " int $b=1; switch($b){case 1: \n $a;break;}" + appendix, errorDto},
                {prefix + " int $b=1; switch($b){case 1:{\n $a;}break;}" + appendix, errorDto},
                {prefix + " int $b=1; switch($b){default:{\n $a;}break;}" + appendix, errorDto},
                {prefix + " for(\n $a=1;;){}" + appendix, errorDto},
                {prefix + " for(;\n $a==1;){}" + appendix, errorDto},
                {prefix + " for(;;++\n $a){}" + appendix, errorDto},
                {prefix + " for(;;){\n $a=1;}" + appendix, errorDto},
                {prefix + "for(\n $a;;){}" + appendix, errorDto},
                {prefix + "foreach([1] as int $v){\n $a=1;}" + appendix, errorDto},
                {prefix + " foreach([1] as int $v){\n $a=1;}" + appendix, errorDto},
                {prefix + " while(\n $a==1){}" + appendix, errorDto},
                {prefix + " while(true)\n $a=1;" + appendix, errorDto},
                {prefix + " while(true){\n $a=1;}" + appendix, errorDto},
                {prefix + " do ; while(\n $a==1);" + appendix, errorDto},
                {prefix + " do \n $a; while(true);" + appendix, errorDto},
                {prefix + " try{\n $a=1;}catch(\\Exception $ex){}" + appendix, errorDto},
                {prefix + " try{}catch(\\Exception $ex){\n $a=1;}" + appendix, errorDto},
                //in expression (ok $a; is also an expression but at the top of the AST)
                {
                        prefix + " !(1+\n $a-\n $a/\n $a*\n $a && \n $a) || \n $a;" + appendix,
                        new ReferenceErrorDto[]{
                                new ReferenceErrorDto("$a", 2, 1),
                                new ReferenceErrorDto("$a", 3, 1),
                                new ReferenceErrorDto("$a", 4, 1),
                                new ReferenceErrorDto("$a", 5, 1),
                                new ReferenceErrorDto("$a", 6, 1),
                                new ReferenceErrorDto("$a", 7, 1),}
                }
        }));
    }
}
