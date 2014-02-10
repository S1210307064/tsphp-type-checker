package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.test.integration.testutils.definition.ADoubleDefinitionTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class DoubleClassDefinitionTest extends ADoubleDefinitionTest
{

    public DoubleClassDefinitionTest(String testString, String theNamespace, String theIdentifier, int howMany) {
        super(testString, theNamespace, theIdentifier, howMany);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        String a = "class a{}";
        String A = "class A{}";
        collection.addAll(getDifferentNamespaces(a + "", "a", 1));
        collection.addAll(getDifferentNamespaces(A + "", "A", 1));
        collection.addAll(getDifferentNamespaces(a + " " + A + "", "A", 1));
        collection.addAll(getDifferentNamespaces(a + " " + a + "", "a", 2));
        collection.addAll(getDifferentNamespaces(a + " " + A + " " + a + "", "a", 2));
        return collection;
    }
}