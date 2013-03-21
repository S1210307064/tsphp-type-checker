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
package ch.tutteli.tsphp.typechecker.test.typecheck;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.typecheck.ATypeCheckErrorTest;
import java.util.ArrayList;
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
public class VariableInitOperatorErrorTest extends ATypeCheckErrorTest
{

    public VariableInitOperatorErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("=", 2, 1)};
        ReferenceErrorDto[] errorCastAssignDto = new ReferenceErrorDto[]{new ReferenceErrorDto("casting", 2, 1)};

        String[] types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "ErrorException", "Exception"};

        for (int i = 0; i < types.length - 1; ++i) {
            collection.add(new Object[]{
                types[i + 1] + " $b; " + types[i] + "\n $a = $b;",
                errorDto
            });
        }
        collection.add(new Object[]{
            types[0] + " $b; "+types[types.length - 1] + "\n $a = $b;",
            errorDto
        });

        types = new String[]{"bool", "bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "ErrorException", "Exception"};
        for (String type : types) {
            collection.add(new Object[]{
                type + " $b; cast resource\n $a = $b;",
                errorDto
            });
            collection.add(new Object[]{
                type + " $b; resource $a\n =() $b;",
                errorCastAssignDto
            });
        }

        return collection;
    }
}