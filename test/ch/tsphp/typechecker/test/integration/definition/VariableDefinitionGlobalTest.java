package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.test.integration.testutils.VariableDeclarationListHelper;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class VariableDefinitionGlobalTest extends ADefinitionSymbolTest
{

    public VariableDefinitionGlobalTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        final List<Object[]> collection = new ArrayList<>();

        final String defaultNamespace = "\\.\\.";
        final String aNamespace = "\\a\\.\\a\\.";

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("", ";", "", "", defaultNamespace,
                null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a{", ";}", "", "", aNamespace, null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a\\b{", ";}", "", "", "\\a\\b\\.\\a\\b\\.", null));


        //Different namespaces
        collection.addAll(Arrays.asList(new Object[][]{
                {
                        "namespace a{int $a=1;} namespace b{float $b=1;}",
                        "\\a\\.\\a\\.int \\a\\.\\a\\.$a "
                                + "\\b\\.\\b\\.float \\b\\.\\b\\.$b"
                },
                {
                        "namespace{int $d=1;} namespace a{float $a=1;} namespace b{int $b=1;}",
                        "\\.\\.int \\.\\.$d "
                                + "\\a\\.\\a\\.float \\a\\.\\a\\.$a "
                                + "\\b\\.\\b\\.int \\b\\.\\b\\.$b"
                },
                {
                        "int $a; bool $b; float $c=1, $d;",
                        "\\.\\.int \\.\\.$a "
                                + "\\.\\.bool \\.\\.$b "
                                + "\\.\\.float \\.\\.$c "
                                + "\\.\\.float \\.\\.$d"
                },
                {
                        "namespace a\\c; int $a; bool $b; float $c=1, $d;",
                        "\\a\\c\\.\\a\\c\\.int \\a\\c\\.\\a\\c\\.$a "
                                + "\\a\\c\\.\\a\\c\\.bool \\a\\c\\.\\a\\c\\.$b "
                                + "\\a\\c\\.\\a\\c\\.float \\a\\c\\.\\a\\c\\.$c "
                                + "\\a\\c\\.\\a\\c\\.float \\a\\c\\.\\a\\c\\.$d"
                },
                {
                        "namespace b{int $a; bool $b; float $e=1.2;} namespace c\\e{ float $c=1, $d;}",
                        "\\b\\.\\b\\.int \\b\\.\\b\\.$a "
                                + "\\b\\.\\b\\.bool \\b\\.\\b\\.$b "
                                + "\\b\\.\\b\\.float \\b\\.\\b\\.$e "
                                + "\\c\\e\\.\\c\\e\\.float \\c\\e\\.\\c\\e\\.$c "
                                + "\\c\\e\\.\\c\\e\\.float \\c\\e\\.\\c\\e\\.$d"
                },
        }));
        return collection;
    }
}
