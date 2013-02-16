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
import ch.tutteli.tsphp.typechecker.test.testutils.AReferenceErrorTest;
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
public class ClassInterfaceExtendsImplementsErrorTest extends AReferenceErrorTest
{

    public ClassInterfaceExtendsImplementsErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getVariations(prefix, appendix, true));
        collection.addAll(getVariations(prefix, appendix, false));
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("a", 2, 1)};
        collection.addAll(Arrays.asList(new Object[][]{
                    {prefix + "class a{} class b implements\n a{}" + appendix, errorDto},
                    {prefix + " class b implements\n a{} class a{}" + appendix, errorDto},}));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix, boolean testClass) {
        String kind = testClass ? "class" : "interface";
        String falseKind = testClass ? "interface" : "class";
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("a", 2, 1)};
        ReferenceErrorDto[] errorDtoTwo = new ReferenceErrorDto[]{
            new ReferenceErrorDto("a", 2, 1),
            new ReferenceErrorDto("b", 3, 1)
        };
        ReferenceErrorDto[] errorDtoThree = new ReferenceErrorDto[]{
            new ReferenceErrorDto("a", 2, 1),
            new ReferenceErrorDto("b", 3, 1),
            new ReferenceErrorDto("c", 4, 1)
        };


        return Arrays.asList(new Object[][]{
                    {prefix + falseKind + " a{} " + kind + " b extends\n a{}" + appendix, errorDto},
                    {prefix + "" + kind + " b extends\n a{}" + falseKind + " \n a{}" + appendix, errorDto},
                    {
                        prefix + falseKind + " a{}{} " + falseKind + " b{} "
                        + kind + " c extends\n a,\n b{}" + appendix, errorDtoTwo
                    },
                    {
                        prefix + falseKind + " a{}{} " + kind + " c extends\n a,\n b{}"
                        + falseKind + " b{} " + appendix, errorDtoTwo
                    },
                    {
                        prefix + kind + " d extends\n a,\n b,\n c{}" + falseKind + " a{}{} "
                        + falseKind + " c{} " + falseKind + " b{} " + appendix, errorDtoThree
                    },
                    {
                        prefix + falseKind + " a{}{} " + kind + " d extends\n a,\n b,\n c{}"
                        + falseKind + " c{} " + falseKind + " b{} " + appendix, errorDtoThree
                    }
                });
    }
}
