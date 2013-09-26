package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceErrorTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

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