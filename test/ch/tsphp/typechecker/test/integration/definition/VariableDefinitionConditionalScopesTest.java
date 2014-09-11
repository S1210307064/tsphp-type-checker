/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.definition;

import ch.tsphp.typechecker.test.integration.testutils.IAdder;
import ch.tsphp.typechecker.test.integration.testutils.TypeHelper;
import ch.tsphp.typechecker.test.integration.testutils.VariableDeclarationListHelper;
import ch.tsphp.typechecker.test.integration.testutils.definition.ADefinitionSymbolTest;
import ch.tsphp.typechecker.utils.ModifierHelper;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

@RunWith(Parameterized.class)
public class VariableDefinitionConditionalScopesTest extends ADefinitionSymbolTest
{

    public VariableDefinitionConditionalScopesTest(String testString, String expectedResult) {
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

        //variable declaration in conditional blocks
        String[][] conditions = new String[][]{
                {"if(true)", ";"},
                {"if(true){", ";}"},
                {"switch($a){case 1:", ";}"},
                {"for(;;)", ";"},
                {"for(;;){", ";}"},
                {"while(true)", ";"},
                {"while(true){", ";}"},};
        for (String[] condition : conditions) {
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                    condition[0], condition[1], "", "", defaultNamespace + "cScope.", null));
            collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("namespace a{" + condition[0],
                    condition[1] + "}", "", "", "" + aNamespace + "cScope.", null));
        }

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "try{ ", ";}catch(\\Exception $e){}", "",
                " " + defaultNamespace + "\\Exception " + defaultNamespace + "$e",
                defaultNamespace + "cScope.", null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("namespace a{try{ ",
                ";}catch(\\Exception $e){}}", "", " " + aNamespace + "\\Exception " + aNamespace + "$e",
                "" + aNamespace + "cScope.", null));

        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "try{}catch(\\Exception $e){", ";}",
                defaultNamespace + "\\Exception " + defaultNamespace + "$e ", "", defaultNamespace + "cScope.", null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a{" + "try{}catch(\\Exception $e){",
                ";}}", "" + aNamespace + "\\Exception " + aNamespace + "$e ", "", aNamespace + "cScope.", null));

        //definition in for  header
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "for(", ";;){}", "", "", defaultNamespace, null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase("namespace a{for(",
                ";;){}}", "", "", aNamespace, null));

        //definition in do while - do while is not a conditional scope
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "do ", ";while(true);", "", "", defaultNamespace, null));
        collection.addAll(VariableDeclarationListHelper.testStringsDefinitionPhase(
                "namespace a{do{ ", ";}while(true);}", "", "", aNamespace, null));


        //definition in foreach header
        TypeHelper.addAllTypesInclModifier(
                new IAdder()
                {
                    @Override
                    public void add(String type, String typeExpected, SortedSet<Integer> modifiers) {
                        String typeModifiers = ModifierHelper.getModifiersAsString(modifiers);
                        collection.add(new Object[]{
                                "foreach($a as " + type + " $v);",
                                defaultNamespace + "cScope." + typeExpected + " " + defaultNamespace + "cScope." +
                                        "$v" + typeModifiers
                        });
                        collection.add(new Object[]{
                                "namespace a{foreach($a as " + type + " $v){}}",
                                aNamespace + "cScope." + typeExpected + " " + aNamespace + "cScope." + "$v" +
                                        typeModifiers
                        });
                    }
                });
        collection.add(
                new Object[]{
                        "foreach($a as string $k => mixed $v){}",
                        defaultNamespace + "cScope.string " + defaultNamespace + "cScope.$k "
                                + defaultNamespace + "cScope.mixed " + defaultNamespace + "cScope.$v"
                });
        collection.add(
                new Object[]{
                        "namespace a{foreach($a as string $k => mixed $v);}",
                        aNamespace + "cScope.string " + aNamespace + "cScope.$k "
                                + aNamespace + "cScope.mixed " + aNamespace + "cScope.$v"
                });

        //definition in catch header
        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{
                    "try{}catch(" + type + " $e){}",
                    defaultNamespace + type + " " + defaultNamespace + "$e"
            });
            collection.add(new Object[]{
                    "namespace a{ try{}catch(" + type + " $e){}}",
                    aNamespace + type + " " + aNamespace + "$e"
            });
        }
        return collection;
    }
}