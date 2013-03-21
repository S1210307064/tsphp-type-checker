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
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
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
public class ResolveClassTypeNotFoundTest extends AReferenceErrorTest
{

    public ResolveClassTypeNotFoundTest(String testString, ReferenceErrorDto[] theErrorDtos) {
        super(testString, theErrorDtos);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getVariations("", "", "\\"));
        collection.addAll(getVariations("namespace{", "}", "\\"));
        collection.addAll(getVariations("namespace a;", "", "\\a\\"));
        collection.addAll(getVariations("namespace a{", "}", "\\a\\"));
        collection.addAll(getVariations("namespace a\\b;", "", "\\a\\b\\"));
        collection.addAll(getVariations("namespace a\\b\\z{", "}", "\\a\\b\\z\\"));
        collection.addAll(Arrays.asList(new Object[][]{
                    //aliases are always absolute        
                    {
                        "namespace b  {class a{} use a as b;\n b $b;} ",
                        new ReferenceErrorDto[]{new ReferenceErrorDto("\\a", 2, 1)}
                    },
                    {
                        "namespace b\\c  {class a{} use a as b;\n b $b;} ",
                        new ReferenceErrorDto[]{new ReferenceErrorDto("\\a", 2, 1)}
                    },
                    {
                        "namespace b\\c\\d  {class a{} use a as b;\n b $b;} ",
                        new ReferenceErrorDto[]{new ReferenceErrorDto("\\a", 2, 1)}
                    },
                    {
                        "namespace a\\b{class a{}} namespace ab\\b{class c{}} "
                        + "namespace ab{use a\\b; \n b\\c $b = new\n b\\c();}",
                        new ReferenceErrorDto[]{
                            new ReferenceErrorDto("\\a\\b\\c", 2, 1),
                            new ReferenceErrorDto("\\a\\b\\c", 3, 1)
                        }
                    },
                    //See bug TSPHP-380
                    {
                        "namespace a{class b{}} namespace a\\b{class a{}} namespace ab\\b\\c{class d{}} "
                        + "namespace ab{use a\\b; \n b\\c\\d $b = new\n b\\c\\d();}",
                        new ReferenceErrorDto[]{
                            new ReferenceErrorDto("\\a\\b\\c\\d", 2, 1),
                            new ReferenceErrorDto("\\a\\b\\c\\d", 3, 1)
                        }
                    },
                     //See bug TSPHP-417
                    {
                        "class a extends\n b{}",
                        new ReferenceErrorDto[]{
                            new ReferenceErrorDto("\\b", 2, 1),
                        }
                    },
                }));

        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix, String namespace) {
        List<Object[]> collection = new ArrayList<>();
        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {

            String fullType = getFullName(namespace, type);
            collection.add(new Object[]{
                        prefix + "\n " + type + "$a;" + appendix,
                        new ReferenceErrorDto[]{new ReferenceErrorDto(fullType, 2, 1)}
                    });

            //Alias
            String aliasFullType = getAliasFullType(type);
            collection.add(new Object[]{
                        prefix + "use " + type + " as test;\n test $a;" + appendix,
                        new ReferenceErrorDto[]{new ReferenceErrorDto(aliasFullType, 2, 1)}
                    });
        }
        return collection;
    }
}
