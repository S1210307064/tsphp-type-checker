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
package ch.tutteli.tsphp.typechecker.test.testutils;

import ch.tutteli.tsphp.typechecker.antlr.TSPHPTypeCheckerDefinition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
public class ConstantHelper
{

    public static Collection<Object[]> testStrings(String prefix, String appendix, String prefixExpected,
          final String scopeName, boolean isDefinitionPhase) {

        List<Object[]> collection = new ArrayList<>();
        String[] types = TypeHelper.getScalarTypes();
        int fin = TSPHPTypeCheckerDefinition.Final;
        for (String type : types) {
            String typeExpected = isDefinitionPhase ? "" : type;
            collection.add(new Object[]{
                        prefix + "const " + type + " a=true;" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "#a" + typeExpected + "|" + fin
                    });
            collection.add(new Object[]{
                        prefix + "const " + type + " a=true, b=false;" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "#a" + typeExpected + "|" + fin + " "
                        + scopeName + type + " " + scopeName + "#b" + typeExpected + "|" + fin
                    });
            collection.add(new Object[]{
                        prefix + "const " + type + " a=1,b=2;" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "#a" + typeExpected + "|" + fin + " "
                        + scopeName + type + " " + scopeName + "#b" + typeExpected + "|" + fin
                    });
            collection.add(new Object[]{
                        prefix + "const " + type + " a=1.0,b=2.0,c=null;" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "#a" + typeExpected + "|" + fin + " "
                        + scopeName + type + " " + scopeName + "#b" + typeExpected + "|" + fin + " "
                        + scopeName + type + " " + scopeName + "#c" + typeExpected + "|" + fin
                    });
            collection.add(new Object[]{
                        prefix + "const " + type + " a=1,b=\"2\",c=null,d='2';" + appendix,
                        prefixExpected + scopeName + type + " " + scopeName + "#a" + typeExpected + "|" + fin + " "
                        + scopeName + type + " " + scopeName + "#b" + typeExpected + "|" + fin + " "
                        + scopeName + type + " " + scopeName + "#c" + typeExpected + "|" + fin + " "
                        + scopeName + type + " " + scopeName + "#d" + typeExpected + "|" + fin
                    });
        }
        return collection;
    }
}
