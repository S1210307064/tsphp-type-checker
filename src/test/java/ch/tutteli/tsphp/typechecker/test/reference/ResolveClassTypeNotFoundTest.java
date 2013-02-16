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

import ch.tutteli.tsphp.typechecker.error.UnresolvedReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.testutils.AUnresolvedReferenceErrorTest;
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
public class ResolveClassTypeNotFoundTest extends AUnresolvedReferenceErrorTest
{

    public ResolveClassTypeNotFoundTest(String testString, UnresolvedReferenceErrorDto[] theErrorDtos) {
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
                        new UnresolvedReferenceErrorDto[]{new UnresolvedReferenceErrorDto("\\a", 2, 1)}
                    },
                    {
                        "namespace b\\c  {class a{} use a as b;\n b $b;} ",
                        new UnresolvedReferenceErrorDto[]{new UnresolvedReferenceErrorDto("\\a", 2, 1)}
                    },
                    {
                        "namespace b\\c\\d  {class a{} use a as b;\n b $b;} ",
                        new UnresolvedReferenceErrorDto[]{new UnresolvedReferenceErrorDto("\\a", 2, 1)}
                    }
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
                        new UnresolvedReferenceErrorDto[]{new UnresolvedReferenceErrorDto(fullType, 2, 1)}
                    });

            //Alias
            String aliasFullType = getAliasFullType(type);
            collection.add(new Object[]{
                        prefix + "use " + type + " as test;\n test $a;" + appendix,
                        new UnresolvedReferenceErrorDto[]{new UnresolvedReferenceErrorDto(aliasFullType, 2, 1)}
                    });
        }
        return collection;
    }
}
