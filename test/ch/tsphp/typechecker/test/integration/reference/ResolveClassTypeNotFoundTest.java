/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
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
        collection.addAll(getVariations("namespace a;", "", "\\a\\"));
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
