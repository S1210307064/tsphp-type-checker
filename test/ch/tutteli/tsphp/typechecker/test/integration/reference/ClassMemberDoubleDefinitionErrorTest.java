package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class ClassMemberDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    public ClassMemberDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {        
        List<Object[]> collection = new ArrayList<>();

        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("class a{", "}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace{ class a{", "}}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a; class b{", "}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a{ class c{", "}}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a\\b; class e{", "}"));
        collection.addAll(VariableDoubleDefinitionErrorTest.getVariations("namespace a\\b\\z{ class m{", "}}"));

        return collection;
    }
}
