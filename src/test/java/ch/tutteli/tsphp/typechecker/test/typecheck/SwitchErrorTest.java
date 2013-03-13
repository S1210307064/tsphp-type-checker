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
public class SwitchErrorTest extends ATypeCheckErrorTest
{

    private static List<Object[]> collection;

    public SwitchErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("switch", 2, 1)};


        String[] types = new String[]{"array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{type + " $b;\n switch($b){}", errorDto});
        }

        errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("$b", 2, 1)};

        types = new String[]{"bool?", "int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"bool $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"int", "int?", "float", "float?", "string", "string?",
            "array", "resource", "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"bool? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"bool?", "int?", "float", "float?", "string", "string?", "array", "resource",
            "object", "Exception", "ErrorException"};
        for (String type : types) {
            collection.add(new Object[]{"int $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"float", "float?", "string", "string?", "array", "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{"int? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"bool?", "int?", "float?", "string", "string?", "array", "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{"float $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"string", "string?", "array", "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{"float? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"bool?", "int?", "string?", "array", "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{"string $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        types = new String[]{"array", "resource", "object"};
        for (String type : types) {
            collection.add(new Object[]{"string? $a; " + type + " $b;switch($a){case\n $b:}", errorDto});
        }

        return collection;
    }
}
