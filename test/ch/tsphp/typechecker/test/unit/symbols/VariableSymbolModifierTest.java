/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class VariableSymbolModifierTest from the TinsPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.unit.symbols;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.symbols.IVariableSymbol;
import ch.tsphp.typechecker.symbols.ModifierSet;
import ch.tsphp.typechecker.symbols.VariableSymbol;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class VariableSymbolModifierTest
{
    private String methodName;
    private int modifierType;

    public VariableSymbolModifierTest(String theMethodName, int theModifierType) {
        methodName = theMethodName;
        modifierType = theModifierType;
    }

    @Test
    public void is_ReturnsTrue() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        IModifierSet set = createModifierSet();
        set.add(modifierType);

        IVariableSymbol variableSymbol = createVariableSymbol(set);
        boolean result = (boolean) variableSymbol.getClass().getMethod(methodName).invoke(variableSymbol);

        assertTrue(methodName + " failed.", result);
    }

    @Test
    public void isNot_ReturnsFalse() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        IModifierSet set = createModifierSet();

        IVariableSymbol variableSymbol = createVariableSymbol(set);
        boolean result = (boolean) variableSymbol.getClass().getMethod(methodName).invoke(variableSymbol);

        assertFalse(methodName + " failed.", result);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        return Arrays.asList(new Object[][]{
                //not yet supported by TSPHP
//                {"isFinal", TSPHPDefinitionWalker.Final},
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

    protected IVariableSymbol createVariableSymbol(IModifierSet set) {
        return new VariableSymbol(mock(ITSPHPAst.class), set, "foo");
    }
}
