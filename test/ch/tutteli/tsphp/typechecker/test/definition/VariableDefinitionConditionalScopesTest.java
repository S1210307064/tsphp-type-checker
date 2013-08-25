/*
 * Copyright 2013 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.definition;

import ch.tutteli.tsphp.typechecker.symbols.ModifierHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.IAdder;
import ch.tutteli.tsphp.typechecker.test.testutils.TypeHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.VariableDeclarationListHelper;
import ch.tutteli.tsphp.typechecker.test.testutils.definition.ADefinitionSymbolTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
        TypeHelper.getAllTypesInclModifier(
                new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet<Integer> modifiers) {
                String typeModifiers = ModifierHelper.getModifiers(modifiers);
                collection.add(new Object[]{
                    "foreach($a as " + type + " $v);",
                    defaultNamespace + "cScope." + typeExpected + " " + defaultNamespace + "cScope." + "$v" + typeModifiers
                });
                collection.add(new Object[]{
                    "namespace a{foreach($a as " + type + " $v){}}",
                    aNamespace + "cScope." + typeExpected + " " + aNamespace + "cScope." + "$v" + typeModifiers
                });
            }
        });
        collection.add(
                new Object[]{
            "foreach($a as string $k => object $v){}",
            defaultNamespace + "cScope.string " + defaultNamespace + "cScope.$k "
            + defaultNamespace + "cScope.object " + defaultNamespace + "cScope.$v"
        });
        collection.add(
                new Object[]{
            "namespace a{foreach($a as string $k => object $v);}",
            aNamespace + "cScope.string " + aNamespace + "cScope.$k "
            + aNamespace + "cScope.object " + aNamespace + "cScope.$v"
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