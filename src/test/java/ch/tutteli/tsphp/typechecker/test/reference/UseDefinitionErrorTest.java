/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
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
package ch.tutteli.tsphp.typechecker.test.reference;

import ch.tutteli.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tutteli.tsphp.typechecker.scopes.INamespaceScope;
import ch.tutteli.tsphp.typechecker.symbols.PseudoTypeSymbol;
import ch.tutteli.tsphp.typechecker.test.testutils.ATypeCheckerReferenceDefinitionErrorTest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
@RunWith(Parameterized.class)
public class UseDefinitionErrorTest extends ATypeCheckerReferenceDefinitionErrorTest
{

    public UseDefinitionErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
        INamespaceScope scope = symbolTable.defineNamespace("\\");
        scope.define(new PseudoTypeSymbol("A"));
        scope.define(new PseudoTypeSymbol("C"));
        scope = symbolTable.defineNamespace("\\A\\");
        scope.define(new PseudoTypeSymbol("B"));
        scope = symbolTable.defineNamespace("\\C\\");
        scope.define(new PseudoTypeSymbol("B"));
        scope = symbolTable.defineNamespace("\\A\\C\\");
        scope.define(new PseudoTypeSymbol("B"));
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        //default namespace;
        collection.addAll(getVariations("", ""));
        collection.addAll(getVariations("namespace{", "}"));
        collection.addAll(getVariations("namespace a;", ""));
        collection.addAll(getVariations("namespace a{", "}"));
        collection.addAll(getVariations("namespace a\\b;", ""));
        collection.addAll(getVariations("namespace a\\b\\z{", "}"));
        return collection;
    }

    public static Collection<Object[]> getVariations(String prefix, String appendix) {
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("B", 3, 1, 2, 1)};
        return Arrays.asList(new Object[][]{
                    {prefix + "use \\A as \n B; use \\C as \n B;" + appendix, errorDto},
                    {prefix + "use \\A as \n B, \\C as \n B;" + appendix, errorDto},
                    {prefix + "use \n \\A\\B; use \\C as \n B;" + appendix, errorDto},
                    {prefix + "use \n \\A\\B, \\C as \n B;" + appendix, errorDto},
                    {prefix + "use \\A as \n B; use \n \\C\\B;" + appendix, errorDto},
                    {prefix + "use \\A as \n B, \n \\C\\B;" + appendix, errorDto},
                    {prefix + "use \n \\A\\C\\B; use \n \\C\\B;" + appendix, errorDto},
                    {prefix + "use \n \\A\\C\\B, \n \\C\\B;" + appendix, errorDto},
                    {prefix + "use \n \\A\\B; use \\A; use \n \\C\\B;" + appendix, errorDto},
                    {prefix + "use \\A as \n B; use \\A; use \n \\C\\B;" + appendix, errorDto},
                    //More than one
                    {prefix + "use \\A as \n B; use \\A; use \n \\C\\B, \\C as \n B;" + appendix,
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("B", 3, 1, 2, 1),
                            new DefinitionErrorDto("B", 4, 1, 2, 1)
                        }
                    },
                    {prefix + "use \\A, \\A as \n B; use \\C; use \n \\C\\B, \\C as \n B;" + appendix,
                        new DefinitionErrorDto[]{
                            new DefinitionErrorDto("B", 3, 1, 2, 1),
                            new DefinitionErrorDto("B", 4, 1, 2, 1)
                        }
                    }
                });
    }
}
