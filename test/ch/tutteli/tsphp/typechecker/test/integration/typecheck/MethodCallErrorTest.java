/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
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
public class MethodCallErrorTest extends ATypeCheckErrorTest
{

    public MethodCallErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("$a", 2, 1)};

        String[] types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "object"};
        //call on a non-object
        for (String type : types) {
            collection.add(new Object[]{type + " $a;\n $a->foo();", errorDto});
        }
   
        AddGeneralTestStrings(collection, false);
        return collection;
    }

    public static void AddGeneralTestStrings(Collection<Object[]> collection, boolean isStatic) {
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("foo()", 2, 1)};
        
        String modifier ="";
        String methodCall = "A $a; $a->\n ";
        if(isStatic){
            modifier = "static ";
            methodCall = "A::\n "; 
        }
        
        collection.addAll(Arrays.asList(new Object[][]{
            //visibility violation
            {"class A{protected "+modifier+" function void foo(){}} " + methodCall + "foo();", errorDto},
            {"class A{private "+modifier+" function void foo(){}} " + methodCall + "foo();", errorDto},
            {
                "class A{private "+modifier+" function void foo(){}} class B extends A{function void bar(){$this->\n foo();}}",
                errorDto
            },
            {
                "class A{private "+modifier+" function void foo(){}} class B extends A{function void bar(){self::\n foo();}}",
                errorDto
            },
            {
                "class A{private "+modifier+" function void foo(){}} class B extends A{function void bar(){parent::\n foo();}}",
                errorDto
            },
            //wrong arguments
            {"class A{public "+modifier+" function void foo(){}} " + methodCall + "foo(1);", errorDto},
            {"class A{public "+modifier+" function void foo(int $a){}} " + methodCall + "foo();", errorDto},
            {"class A{public "+modifier+" function void foo(int $a){}} " + methodCall + "foo('1');", errorDto},
            {"class A{public "+modifier+" function void foo(int $a, string $b){}} " + methodCall + "foo(1,[1]);", errorDto}
        }));
    }
}
