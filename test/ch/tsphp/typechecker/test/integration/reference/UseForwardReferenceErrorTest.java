/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class UseForwardReferenceErrorTest extends AReferenceDefinitionErrorTest
{

    public UseForwardReferenceErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        //default namespace;
        collection.addAll(getVariationsForDefaultNamespace("namespace{", "}"));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));
        return collection;
    }

    public static Collection<Object[]> getVariationsForDefaultNamespace(String prefix, String appendix) {
        return getVariations(prefix, appendix, new String[]{
                "\\A\\B",
                "\\A\\C\\B",
                "A\\B",
                "A\\C\\B"});
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        return getVariations(prefix, appendix, new String[]{
                "\\B",
                "\\A\\B",
                "\\A\\C\\B",
                "A\\B",
                "A\\C\\B"});
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix, String[] types) {
        List<Object[]> collection = new ArrayList<>();

        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("B", 2, 1, "B", 3, 1)};
        DefinitionErrorDto[] twoErrorDto = new DefinitionErrorDto[]{
                new DefinitionErrorDto("B", 2, 1, "B", 4, 1),
                new DefinitionErrorDto("B", 3, 1, "B", 4, 1)
        };

        for (String type : types) {

            String namespaceName = "";
            int lastBackslash = type.lastIndexOf("\\");
            if (lastBackslash != -1) {
                namespaceName = type.substring(0, lastBackslash);
                if (namespaceName.length() != 0 && namespaceName.substring(0, 1).equals("\\")) {
                    namespaceName = namespaceName.substring(1);
                }
            }
            String newAppendix = appendix + "namespace " + namespaceName + "{ class B{}} ";

            collection.addAll(Arrays.asList(new Object[][]{
                    {prefix + "\n B $a; use \n " + type + ";" + newAppendix, errorDto},
                    {prefix + "\n B $a; use " + type + " as \n B;" + newAppendix, errorDto},
                    //More than one
                    {prefix + "\n B $a; \n B $b; use \n " + type + ";" + newAppendix, twoErrorDto},
                    {prefix + "\n B $a;\n B $b;  use " + type + " as \n B;" + newAppendix, twoErrorDto},}));
        }

        return collection;
    }
}
