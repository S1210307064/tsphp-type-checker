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
public class ThisSelfParentOutsideClassTest extends AReferenceErrorTest
{

    public ThisSelfParentOutsideClassTest(String testString, ReferenceErrorDto[] theErrorDtos) {
        super(testString, theErrorDtos);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        //in global scope
        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));

        //in functions
        collection.addAll(getVariations("function void foo(){", "}"));
        collection.addAll(getVariations("namespace a;function void foo(){", "}"));
        collection.addAll(getVariations("namespace a\\b\\z{function void foo(){", "}}"));

        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();

        ReferenceErrorDto[] thisErrorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("$this", 2, 1)};
        ReferenceErrorDto[] selfErrorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("self", 2, 1)};
        ReferenceErrorDto[] parentErrorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("parent", 2, 1)};

        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "\n $this->a;" + appendix, thisErrorDto},
                {prefix + "\n $this->foo();" + appendix, thisErrorDto},
                {prefix + "\n self::$a;" + appendix, selfErrorDto},
                {prefix + "\n self::foo();" + appendix, selfErrorDto},
                {prefix + "\n parent::$a;" + appendix, parentErrorDto},
                {prefix + "\n parent::foo();" + appendix, parentErrorDto}
        }));
        return collection;
    }
}