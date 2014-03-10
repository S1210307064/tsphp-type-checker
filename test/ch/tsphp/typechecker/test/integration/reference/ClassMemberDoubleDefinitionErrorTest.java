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
import java.util.Collection;
import java.util.List;

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
