/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

/*
 * This class is based on the class MethodSymbolReturnTypeModifierTest from the TinsPHP project.
 * TSPHP is also published under the Apache License 2.0
 * For more information see http://tsphp.ch/wiki/display/TINS/License
 */

package ch.tsphp.typechecker.test.unit.symbols;

import ch.tsphp.common.IScope;
import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.common.symbols.modifiers.IModifierSet;
import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.scopes.IScopeHelper;
import ch.tsphp.typechecker.symbols.IMethodSymbol;
import ch.tsphp.typechecker.symbols.MethodSymbol;
import ch.tsphp.typechecker.symbols.ModifierSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

@RunWith(Parameterized.class)
public class MethodSymbolReturnTypeModifierTest
{
    private String methodName;
    private int modifierType;

    public MethodSymbolReturnTypeModifierTest(String theMethodName, int theModifierType) {
        methodName = theMethodName;
        modifierType = theModifierType;
    }

    @Test
    public void is_ReturnsTrue() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        IModifierSet set = createModifierSet();
        set.add(modifierType);

        IMethodSymbol methodSymbol = createMethodSymbol(set);
        boolean result = (boolean) methodSymbol.getClass().getMethod(methodName).invoke(methodSymbol);

        //the following three modifiers are return type modifiers and thus the expected result should be true
        //all other modifier are method modifiers the expected result will be false even though the return type modifier
        //set comprises those modifiers.
        boolean is = methodName.equals("isAlwaysCasting")
                || methodName.equals("isFalseable")
                || methodName.equals("isNullable");

        assertEquals(methodName + " failed.", is, result);
    }

    @Test
    public void isNot_ReturnsFalse() throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        IModifierSet set = createModifierSet();

        IMethodSymbol methodSymbol = createMethodSymbol(set);
        boolean result = (boolean) methodSymbol.getClass().getMethod(methodName).invoke(methodSymbol);

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

    protected IMethodSymbol createMethodSymbol(IModifierSet set) {
        return new MethodSymbol(mock(IScopeHelper.class), mock(ITSPHPAst.class), mock(IModifierSet.class), set,
                "foo", mock(IScope.class));
    }
}
