package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.common.ISymbol;
import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class MissingImplementationErrorTest extends ATypeCheckErrorTest
{
    private Set<String> missingIdentifiers;

    public MissingImplementationErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions,
            String[] theMissingIdentifiers) {
        super(testString, expectedLinesAndPositions);
        missingIdentifiers = new HashSet<>(Arrays.asList(theMissingIdentifiers));
    }

    @SuppressWarnings({"unchecked", "ThrowableResultOfMethodCallIgnored"})
    @Test
    public void test() throws RecognitionException {
        check();
        ArgumentCaptor<Set> symbolsCaptor = ArgumentCaptor.forClass(Set.class);
        verify(typeCheckErrorReporter).missingAbstractImplementations(any(ITSPHPAst.class), symbolsCaptor.capture());
        Set<ISymbol> symbols = (Set<ISymbol>) symbolsCaptor.getValue();
        Set<String> identifiers = new HashSet<>();
        for (ISymbol symbol : symbols) {
            identifiers.add(symbol.getName());
        }
        assertThat(identifiers, is(missingIdentifiers));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        ReferenceErrorDto[] errorDto = new ReferenceErrorDto[]{new ReferenceErrorDto("A", 2, 1)};
        return Arrays.asList(new Object[][]{
                {
                        "abstract class B{abstract function void foo();} class\n A extends B{}",
                        errorDto, new String[]{"foo()"}
                },
                //more than one missing
                {
                        "abstract class B{abstract function void foo(); abstract function void bar();} "
                                + "class\n A extends B{}",
                        errorDto, new String[]{"foo()", "bar()"}
                },
                {
                        "abstract class B{abstract function void foo(); function void foo2(){} "
                                + "abstract function void bar();} class\n A extends B{}",
                        errorDto, new String[]{"foo()", "bar()"}
                },
                //over multiple levels
                {
                        "abstract class B{abstract function void foo();} "
                                + "abstract class C extends B{} "
                                + "class\n A extends C{}",
                        errorDto, new String[]{"foo()"}
                },
                {
                        "abstract class B{abstract function void foo();} "
                                + "abstract class C extends B{function void foo(){} abstract function void bar();} "
                                + "abstract class D extends C{} "
                                + "class\n A extends D{}",
                        errorDto, new String[]{"bar()"}
                },
                //in multiple levels, more than one missing
                {
                        "abstract class B{abstract function void foo();} "
                                + "abstract class C extends B{abstract function void bar();} "
                                + "class\n A extends C{}",
                        errorDto, new String[]{"foo()", "bar()"}
                },
                {
                        "abstract class B{abstract function void foo(); abstract function void baz();} "
                                + "abstract class C extends B{abstract function void bar();} "
                                + "class\n A extends C{}",
                        errorDto, new String[]{"foo()", "bar()", "baz()"}
                },
        });
    }

    @Override
    protected ITypeCheckErrorReporter createTypeCheckErrorReporter() {
        return spy(super.createTypeCheckErrorReporter());
    }
}
