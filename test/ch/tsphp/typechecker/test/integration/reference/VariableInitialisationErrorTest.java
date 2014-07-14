/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.reference;

import ch.tsphp.common.ITSPHPAst;
import ch.tsphp.typechecker.error.DefinitionErrorDto;
import ch.tsphp.typechecker.error.ITypeCheckerErrorReporter;
import ch.tsphp.typechecker.test.integration.testutils.reference.AReferenceDefinitionErrorTest;
import org.antlr.runtime.RecognitionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.exceptions.base.MockitoAssertionError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class VariableInitialisationErrorTest extends AReferenceDefinitionErrorTest
{

    private static List<Object[]> collection;

    private IInitialisedVerifier verifier;

    public VariableInitialisationErrorTest(String testString, DefinitionErrorDto[] expectedLinesAndPositions,
            IInitialisedVerifier theVerifier) {
        super(testString, expectedLinesAndPositions);
        verifier = theVerifier;
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void test() throws RecognitionException {
        check();

        try {
            verifier.check(typeCheckErrorReporter);
        } catch (MockitoAssertionError e) {
            System.err.println(testString + " failed.");
            throw e;
        }
    }

    @Parameterized.Parameters
    public static Collection<Object[]> testStrings() {
        collection = new ArrayList<>();

        //global
        addVariations("", "");
        addVariations("namespace{", "}");
        addVariations("namespace a;", "");
        addVariations("namespace a{", "}");
        addVariations("namespace a\\b;", "");
        addVariations("namespace a\\b\\z{", "}");

        //functions
        addVariations("function void foo(){", "}");
        addVariations("namespace{function void foo(){", "}}");
        addVariations("namespace a;function void foo(){", "}");
        addVariations("namespace a{function void foo(){", "}}");
        addVariations("namespace a\\b;function void foo(){", "}");
        addVariations("namespace a\\b\\z{function void foo(){", "}}");

        //methods
        addVariations("class a{ function void foo(){", "}}");
        addVariations("namespace{ class a{ function void foo(){", "}}}");
        addVariations("namespace a; class a{ function void foo(){", "}}");
        addVariations("namespace a{ class a { function void foo(){", "}}}");
        addVariations("namespace a\\b; class a{ function void foo(){", "}}");
        addVariations("namespace a\\b\\z{ class a{ function void foo(){", "}}}");

        return collection;
    }

    private static void addVariations(String prefix, String appendix) {
        DefinitionErrorDto[] errorDto = new DefinitionErrorDto[]{new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1)};
        DefinitionErrorDto[] twoErrorDto = new DefinitionErrorDto[]{
                new DefinitionErrorDto("$a", 2, 1, "$a", 3, 1),
                new DefinitionErrorDto("$a", 2, 1, "$a", 4, 1)
        };
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "int\n $a;\n" + " $a;" + appendix, errorDto, new NotVerifier()},
                {prefix + "int $b,\n $a;\n $a; " + appendix, errorDto, new NotVerifier()},
                {prefix + "int $b = 1,\n $a;\n $a;" + appendix, errorDto, new NotVerifier()},
                {prefix + "int \n $a; if(true){\n $a;}" + appendix, errorDto, new NotVerifier()},
                {prefix + "int \n $a; while(true){\n $a;}" + appendix, errorDto, new NotVerifier()},
                {prefix + "int \n $a; switch(1){case 1:\n $a;}" + appendix, errorDto, new NotVerifier()},
                {prefix + "int \n $a; switch(1){default:\n $a;}" + appendix, errorDto, new NotVerifier()},
                {prefix + "int\n $a; if(true){$a = 1;}\n $a;" + appendix, errorDto, new PartiallyVerifier()},
                {prefix + "int\n $a; while(true){$a = 1;}\n $a;" + appendix, errorDto, new PartiallyVerifier()},
                {prefix + "int\n $a; for(;;){$a = 1;}\n $a;" + appendix, errorDto, new PartiallyVerifier()},
                {prefix + "for(int\n $a;\n $a < 10; ){}" + appendix, errorDto, new NotVerifier()},
                {
                        prefix + "int\n $a; foreach([1] as object $v){$a = 1;}\n $a;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                {
                        prefix + "int\n $a; try{$a = 1;}catch(\\Exception $e){}\n $a;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                {
                        prefix + "int\n $a; try{}catch(\\Exception $e){$a = 1;}\n $a;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                //not all catch blocks initialise
                {
                        prefix + "int\n $a; try{$a = 1;}catch(\\ErrorException $e2){}" +
                                "catch(\\Exception $e){$a = 1;}\n $a;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                {
                        prefix + "int\n $a; try{$a = 1;}catch(\\ErrorException $e2){$a = 1;}" +
                                "catch(\\Exception $e){}\n $a;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                //needs default case to be sure that it returns
                {
                        prefix + "int\n $a; switch(1){case 1: $a=1;}\n $a * 96;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                //break before return/throw statement
                {
                        prefix + "int\n $a; switch(1){case 1: break; $a=1;}\n $a - 25;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                {
                        prefix + "int\n $a; switch(1){default: break;$a=1;} \n $a + 96;" + appendix,
                        errorDto, new PartiallyVerifier()
                },
                //not all cases return/throw
                {
                        prefix + "int\n $a; switch(1){case 1: break; default: $a = 1;}\n $a * 78;" + appendix,
                        errorDto, new PartiallyVerifier()
                },

                //More than one
                {prefix + "int\n $a;\n $a; \n $a; " + appendix, twoErrorDto, new NotVerifier(2)},
                {prefix + "int\n $a, $b;\n $a; \n $a; " + appendix, twoErrorDto, new NotVerifier(2)},
                {prefix + "int\n $a, $b=1;\n $a; \n $a; " + appendix, twoErrorDto, new NotVerifier(2)},
                {
                        prefix + "int $c, $d = 2,\n $a, $b=1; if(true){while(true) $a=1;\n $a;} "
                                + "if(false){$a = 1;} for(;;){}\n $a; " + appendix,
                        twoErrorDto, new PartiallyVerifier(2)
                },
                {prefix + "for(int\n $a;\n $a < 10;\n $a++){}" + appendix, twoErrorDto, new NotVerifier(2)},
        }));
    }

    @Override
    protected ITypeCheckerErrorReporter createTypeCheckerErrorReporter() {
        return spy(super.createTypeCheckerErrorReporter());
    }

    private static class PartiallyVerifier implements IInitialisedVerifier
    {
        private int times;

        public PartiallyVerifier() {
            this(1);
        }

        public PartiallyVerifier(int howManyTimes) {
            times = howManyTimes;
        }

        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void check(ITypeCheckerErrorReporter typeCheckErrorReporter) {
            verify(typeCheckErrorReporter, times(times)).variablePartiallyInitialised(any(ITSPHPAst.class),
                    any(ITSPHPAst.class));
        }
    }

    private static class NotVerifier implements IInitialisedVerifier
    {
        private int times;

        public NotVerifier() {
            this(1);
        }

        public NotVerifier(int howManyTimes) {
            times = howManyTimes;
        }

        @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
        @Override
        public void check(ITypeCheckerErrorReporter typeCheckErrorReporter) {
            verify(typeCheckErrorReporter, times(times)).variableNotInitialised(any(ITSPHPAst.class), any(ITSPHPAst
                    .class));
        }
    }

    private static interface IInitialisedVerifier
    {
        void check(ITypeCheckerErrorReporter typeCheckErrorReporter);
    }
}
