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

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.AReferenceDefinitionErrorTest;
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
public class UseForwardReferenceErrorTest extends AReferenceDefinitionErrorTest
{

    public UseForwardReferenceErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        //default namespace;
        collection.addAll(getVariationsForDefaultNamespace("namespace{", "}"));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));
        return collection;
    }

    public static Collection<Object[]> getVariationsForDefaultNamespace(String prefix, String appendix) {
        return getVariations(prefix, appendix, new String[]{
                    "\\A\\B",
                    "\\A\\C\\B",
                    "A\\B",
                    "A\\C\\B"});
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        return getVariations(prefix, appendix, new String[]{
                    "\\B",
                    "\\A\\B",
                    "\\A\\C\\B",
                    "A\\B",
                    "A\\C\\B"});
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix, String[] types) {
        List<Object[]> collection = new ArrayList<>();

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("B", 2, 1, "B", 3, 1)};
        DefinitionErrorDto[] twoErrorDto = new DefinitionErrorDto[]{
            new DefinitionErrorDto("B", 2, 1, "B", 4, 1),
            new DefinitionErrorDto("B", 3, 1, "B", 4, 1)
        };

        for (String type : types) {

            String namespaceName = "";
            int lastBackslash = type.lastIndexOf("\\");
            if (lastBackslash != -1) {
                namespaceName = type.substring(0, lastBackslash);
                if (namespaceName.length() != 0 && namespaceName.substring(0, 1).equals("\\")) {
                    namespaceName = namespaceName.substring(1);
                }
            }
            String newAppendix = appendix + "namespace " + namespaceName + "{ class B{}} ";

            collection.addAll(Arrays.asList(new Object[][]{
                        {prefix + "\n B $a; use \n " + type + ";" + newAppendix, errorDto},
                        {prefix + "\n B $a; use " + type + " as \n B;" + newAppendix, errorDto},
                        //More than one
                        {prefix + "\n B $a; \n B $b; use \n " + type + ";" + newAppendix, twoErrorDto},
                        {prefix + "\n B $a;\n B $b;  use " + type + " as \n B;" + newAppendix, twoErrorDto},}));
        }

        return collection;
    }
}
