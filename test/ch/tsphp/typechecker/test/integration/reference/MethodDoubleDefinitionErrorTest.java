/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.MethodModifierHelper;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;

@RunWith(Parameterized.class)
public class MethodDoubleDefinitionErrorTest extends AReferenceDefinitionErrorTest
{

    public MethodDoubleDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
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

        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        Collection<Object[]> collection = new ArrayList<>();
        collection.addAll(getModifiers(prefix, appendix));
        collection.addAll(FunctionDoubleDefinitionErrorTest.getVariations(prefix + "class a{ ", appendix + "}"));
        return collection;
    }

    private static Collection<Object[]> getModifiers(String prefix, String appendix) {
        Collection<Object[]> collection = new ArrayList<>();
        String[] variations = MethodModifierHelper.getVariations();

        final String newPrefix = prefix + "class a{ ";
        final String newAppendix = appendix + "}";

        final DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("foo()", 2, 1, "foo()",
                3, 1)};
        final DefinitionErrorDto[] errorDtoTwo = new DefinitionErrorDto[]{
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 3, 1),
                new DefinitionErrorDto("foo()", 2, 1, "foo()", 4, 1)
        };

        String foo = " function void\n foo(){} ";

        //it does not matter if modifier are different
        for (String modifier : variations) {
            collection.add(new Object[]{
                    newPrefix + modifier + foo + modifier + foo + newAppendix,
                    errorDto
            });
            collection.add(new Object[]{
                    newPrefix + modifier + foo + modifier + foo + modifier + foo + newAppendix,
                    errorDtoTwo
            });
        }
        variations = MethodModifierHelper.getAbstractVariations();

        foo = " function void\n foo(); ";
        for (String modifier : variations) {
            collection.add(new Object[]{
                    newPrefix + modifier + foo + modifier + foo + newAppendix,
                    errorDto
            });
            collection.add(new Object[]{
                    newPrefix + modifier + foo + modifier + foo + modifier + foo + newAppendix,
                    errorDtoTwo
            });
        }
        return collection;
    }
}
