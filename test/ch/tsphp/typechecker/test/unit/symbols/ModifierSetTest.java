/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class ModifierSetTest from the TinsPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.unit.symbols;

import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.symbols.ModifierSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class ModifierSetTest
{
    private String methodName;
    private int modifierType;

    public ModifierSetTest(String theMethodName, int theModifierType) {
        methodName = theMethodName;
        modifierType = theModifierType;
    }

    @Test
    public void is_ReturnsTrue() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        //no arrange necessary

        IModifierSet set = createModifierSet();
        set.add(modifierType);
        boolean result = (boolean) set.getClass().getMethod(methodName).invoke(set);

        assertTrue(methodName + " failed.", result);
    }

    @Test
    public void isNot_ReturnsFalse() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        //no arrange necessary

        IModifierSet set = createModifierSet();
        boolean result = (boolean) set.getClass().getMethod(methodName).invoke(set);

        assertFalse(methodName + " failed.", result);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                {"isAbstract", TSPHPDefinitionWalker.Abstract},
                {"isFinal", TSPHPDefinitionWalker.Final},
                {"isStatic", TSPHPDefinitionWalker.Static},
                {"isPublic", TSPHPDefinitionWalker.Public},
                {"isProtected", TSPHPDefinitionWalker.Protected},
                {"isPrivate", TSPHPDefinitionWalker.Private},
                {"isAlwaysCasting", TSPHPDefinitionWalker.Cast},
                {"isFalseable", TSPHPDefinitionWalker.LogicNot},
                {"isNullable", TSPHPDefinitionWalker.QuestionMark},
        });
    }

    protected IModifierSet createModifierSet() {
        return new ModifierSet();
    }
}
