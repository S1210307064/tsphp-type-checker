package ch.tutteli.tsphp.typechecker.test.integration.typecheck;

import ch.tutteli.tsphp.common.ITSPHPAst;
import ch.tutteli.tsphp.common.ITypeSymbol;
import ch.tutteli.tsphp.typechecker.error.ITypeCheckErrorReporter;
import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;
import ch.tutteli.tsphp.typechecker.test.integration.testutils.typecheck.ATypeCheckErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class ClassMemberInitialValueErrorTest extends ATypeCheckErrorTest
{

    public ClassMemberInitialValueErrorTest(String testString, ReferenceErrorDto[] expectedLinesAndPositions) {
        super(testString, expectedLinesAndPositions);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void test() throws RecognitionException {
        check();
        verify(typeCheckErrorReporter).wrongClassMemberInitialValue(
                any(ITSPHPAst.class), any(ITSPHPAst.class), any(ITypeSymbol.class));
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        List<Object[]> collection = new ArrayList<>();
        Object[][] typeAndInitialValues = new Object[][]{
                {"bool", new String[]{"1", "1.2", "'1.2'", "null"}},
                {"bool?", new String[]{"1", "1.2", "'1.2'"}},
                {"int", new String[]{"1.2", "'1.2'", "null"}},
                {"int?", new String[]{"1.2", "'1.2'"}},
                {"float", new String[]{"'1.2'", "null"}},
                {"float?", new String[]{"'1.2'"}},
        };

        for (Object[] tuple : typeAndInitialValues) {
            String type = (String) tuple[0];
            for (String value : (String[]) tuple[1]) {
                collection.add(new Object[]{
                        "class A{ cast " + type + "\n $a = " + value + "; }",
                        new ReferenceErrorDto[]{new ReferenceErrorDto("$a", 2, 1)}
                });
            }
        }

        return collection;
    }

    @Override
    protected ITypeCheckErrorReporter createTypeCheckErrorReporter() {
        return spy(super.createTypeCheckErrorReporter());
    }
}
