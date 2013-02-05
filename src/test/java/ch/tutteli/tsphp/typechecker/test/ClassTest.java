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
package ch.tutteli.tsphp.typechecker.test;

import ch.tutteli.tsphp.typechecker.test.utils.ATypeCheckerTest;
import ch.tutteli.tsphp.typechecker.test.utils.TypeHelper;
import java.util.ArrayList;
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
public class ClassTest extends ATypeCheckerTest
{

    public ClassTest(String testString, String expectedResult) {
        super(testString, expectedResult);
    }

    @Test
    public void test() throws RecognitionException {
        check();
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();

        collection.add(new Object[]{"class a{}", "default.a"});
        collection.add(new Object[]{
            "class a{} class b{} class c extends a\\b{}",
            "default.a default.b default.a\\b default.c"
        });
        collection.add(new Object[]{
            "namespace x{class a{}} namespace y{class b{}} namespace z{class c extends a\\b{}} namespace{class d{}}",
            "x.a y.b z.a\\b z.c default.d"
        });

        String[] types = TypeHelper.getClassInterfaceTypes();
        for (String type : types) {
            collection.add(new Object[]{"class a extends " + type + "{}", "default." + type + " default.a"});
            collection.add(new Object[]{
                        "class a extends " + type + "," + type + "{}",
                        "default." + type + " default." + type + " default.a"
                    });
            collection.add(new Object[]{
                        "class a extends " + type + "," + type + "," + type + "{}",
                        "default." + type + " default." + type + " default." + type + " default.a"
                    });

            collection.add(new Object[]{"class a implements " + type + "{}", "default." + type + " default.a"});
            collection.add(new Object[]{
                        "class a implements " + type + "," + type + "{}",
                        "default." + type + " default." + type + " default.a"
                    });
            collection.add(new Object[]{
                        "class a implements " + type + "," + type + "," + type + "{}",
                        "default." + type + " default." + type + " default." + type + " default.a"
                    });

            collection.add(new Object[]{
                        "class a extends " + type + " implements " + type + "{}",
                        "default." + type + " default." + type + " default.a"
                    });
        }


        return collection;
    }
}
