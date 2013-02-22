/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.reference;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.reference.AReferenceErrorTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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

        //global constants
        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");

        //functions
        addVariations("function void foo(){", "}");
        addVariations("namespace{function void foo(){", "}}");
        addVariations("namespace a;function void foo(){", "}");
        addVariations("namespace a{function void foo(){", "}}");
        addVariations("namespace a\\b;function void foo(){", "}");
        addVariations("namespace a\\b\\z{function void foo(){", "}}");

        //methods
        addVariations("class a{ function void foo(){", "}}");
        addVariations("namespace{ class a{ function void foo(){", "}}}");
        addVariations("namespace a; class a{ function void foo(){", "}}");
        addVariations("namespace a{ class a { function void foo(){", "}}}");
        addVariations("namespace a\\b; class a{ function void foo(){", "}}");
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
                    {prefix + " int $b; switch($b){case 1: \n $a;break;}" + appendix, errorDto},
                    {prefix + " int $b; switch($b){case 1:{\n $a;}break;}" + appendix, errorDto},
                    {prefix + " int $b; switch($b){default:{\n $a;}break;}" + appendix, errorDto},
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
