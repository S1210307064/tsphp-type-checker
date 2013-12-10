package ch.tutteli.tsphp.typechecker.test.integration.reference;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class FunctionDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{
    public FunctionDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        Collection<Object[]> collection = new ArrayList<>();

        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1)};

        collection.addAll(Arrays.asList(new Object[][]{
                {
                        "namespace{function void\n foo(){}} namespace{function void\n foo(){}}",
                        errorDto
                },
                {
                        "namespace a{function void\n foo(){}} namespace a{function void\n foo(){}}",
                        errorDto
                },
                {
                        "namespace {function void\n foO(){}} namespace{function void\n foo(){}}",
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("foO()", 2, 1, "foo()", 3, 1)
                        }
                },
                {
                        "namespace a{function void\n foo(){}} namespace a{function void\n FOO(){}} "
                                + "namespace a{function void\n foO(){}} namespace a{function void\n foo(){}}",
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("foo()", 2, 1, "FOO()", 3, 1),
                                new DefinitionErrorDto("foo()", 2, 1, "foO()", 4, 1),
                                new DefinitionErrorDto("foo()", 2, 1, "foo()", 5, 1)
                        }
                }
        }));

        return collection;
    }

    public static Collection<Object[]> getVariations(final String prefix, final String appendix) {
        Collection<Object[]> collection = new ArrayList<>();
        final DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "foo()",
                3, 1)};
        final DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1),
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 4, 1)
        };

        List<String> types = TypeHelper.getPrimitiveTypes();
        for (String type : types) {
            //it does not matter if return values are different
            collection.add(new Object[]{
                    prefix + "function " + type + "\n foo(){return 1;} function void \n foo(){}" + appendix,
                    errorDto
            });
            collection.add(new Object[]{
                    prefix + "function " + type + "\n foo(){return 1;} function void \n foo(){} "
                            + "function int \n foo(){return 1;}" + appendix,
                    errorDtoTwo
            });

            //And since PHP does not support method overloading, also the parameter does not matter
            collection.add(new Object[]{
                    prefix + " function void \n foo(" + type + " $b){return 1;} function void \n foo(){}" + appendix,
                    errorDto
            });
            collection.add(new Object[]{
                    prefix + " function void \n foo(" + type + " $b){return 1;} function void \n foo(){}"
                            + "function void \n foo(int $a){}" + appendix,
                    errorDtoTwo
            });
        }

        //case insensitive
        collection.addAll(Arrays.asList(new Object[][]{
                {
                        "function void\n foo(){} function void\n Foo(){}",
                        new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "Foo()", 3, 1)}
                },
                {
                        "function void\n foo(){} function void\n Foo(){} function void\n fOo(){}",
                        new DefinitionErrorDto[]{
                                new DefinitionErrorDto("foo()", 2, 1, "Foo()", 3, 1),
                                new DefinitionErrorDto("foo()", 2, 1, "fOo()", 4, 1)
                        }
                }
        }));
        return collection;
    }
}
