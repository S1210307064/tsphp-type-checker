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
public class NoParentTest extends AReferenceErrorTest
{

    public NoParentTest(String testString, ReferenceErrorDto[] theErrorDtos) {
        super(testString, theErrorDtos);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        //methods
        collection.addAll(getVariations("class a{ function void foo(){", "}}"));
        collection.addAll(getVariations("namespace{class a{ function void foo(){", "}}}"));
        collection.addAll(getVariations("namespace a;class a{ function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a{class a{ function void foo(){", "}}}"));
        collection.addAll(getVariations("namespace a\\b;class a{ function void foo(){", "}}"));
        collection.addAll(getVariations("namespace a\\b\\z{class a{ function void foo(){", "}}}"));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();

        ReferenceErrorDto[] parentErrorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("parent", 2, 1)};

        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "\n parent::$a;" + appendix, parentErrorDto},
                {prefix + "\n parent::foo();" + appendix, parentErrorDto}
        }));
        return collection;
    }
}