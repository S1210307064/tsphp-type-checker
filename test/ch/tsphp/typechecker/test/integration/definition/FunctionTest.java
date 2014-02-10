package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.symbols.ModifierHelper;
import ch.tsphp.typechecker.test.integration.testutils.IAdder;
import ch.tsphp.typechecker.test.integration.testutils.ParameterListHelper;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

@RunWith(Parameterized.class)
public class FunctionTest extends ADefinitionSymbolTest
{

    public FunctionTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        final List<Object[]> collection = new ArrayList<>();

        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet modifiers) {
                String typeModifiers = ModifierHelper.getModifiers(modifiers);
                collection.add(new Object[]{
                        "function " + type + " get(){}",
                        "\\.\\." + typeExpected + " \\.\\.get()" + typeModifiers
                });
            }
        });


        collection.addAll(ParameterListHelper.getTestStrings(
                "function void foo(", "){}", "\\.\\.void \\.\\.foo() ", "\\.\\.foo().", true));

        return collection;
    }
}
