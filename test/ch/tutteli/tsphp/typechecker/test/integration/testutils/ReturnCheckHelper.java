package ch.tutteli.tsphp.typechecker.test.integration.testutils;

import ch.tutteli.tsphp.typechecker.error.ReferenceErrorDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ReturnCheckHelper
{

    public static Collection<Object[]> getTestStringVariations(String prefix, String appendix) {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getTestStringVariations(prefix, appendix, "return 12;"));
        collection.addAll(getTestStringVariations(prefix, appendix, "throw new Exception();"));
        collection.addAll(Arrays.asList(new Object[][]{
                {prefix + "if(true){return 1;}else{throw new Exception();}" + appendix},
                {prefix + "if(true){throw new Exception();}else{return 0;}" + appendix},
        }));
        return collection;
    }

    private static Collection<Object[]> getTestStringVariations(String prefix, String appendix, String statement) {
        return Arrays.asList(new Object[][]{
                {prefix + statement + appendix},
                {prefix + "{" + statement + "}" + appendix},
                {prefix + "if(true){" + statement + "}else{" + statement + "}" + appendix},
                {prefix + "int $a; " + statement + appendix},
                {prefix + "int $a; {" + statement + "} int $b;" + appendix},
                {prefix + "do{" + statement + "}while(true);" + appendix},
                {prefix + "switch(1){default:" + statement + "}" + appendix},
                {prefix + "switch(1){case 1:" + statement + " default:" + statement + "}" + appendix},
                {
                        prefix + "switch(1){case 1:" + statement + "case 2: " + statement
                                + " default:" + statement + "}" + appendix
                },
                {prefix + "switch(1){case 1: default:" + statement + "}" + appendix},
                {prefix + "try{" + statement + "}catch(\\Exception $e){" + statement + "}" + appendix},
                {
                        prefix + "try{" + statement + "}"
                                + "catch(\\ErrorException $e){" + statement + "}"
                                + "catch(\\Exception $e2){" + statement + "}" +
                                appendix
                },
                {prefix + "int $a; if(true){ }" + statement + appendix},
                {
                        prefix + "int $a; if(true){ } int $b;"
                                + "if(true){"
                                + "if(true){}"
                                + "if(true){" + statement + "}else{" + statement + "}"
                                + "}else{ "
                                + "while(true){}" + statement
                                + "}" + appendix
                }
        });
    }

    public static Collection<Object[]> getReferenceErrorPairs(String prefix, String appendix,
            ReferenceErrorDto[] errorDto) {
        List<Object[]> collection = new ArrayList<>();
        collection.addAll(getErrorPairVariations(prefix, appendix, "return 12;", errorDto));
        collection.addAll(getErrorPairVariations(prefix, appendix, "throw new Exception();", errorDto));
        return collection;

    }

    public static Collection<Object[]> getErrorPairVariations(String prefix, String appendix, String statement,
            ReferenceErrorDto[] errorDto) {
        return Arrays.asList(new Object[][]{
                {prefix + "" + appendix, errorDto},
                {prefix + "int $a;" + appendix, errorDto},
                {prefix + "int $a; float $f;" + appendix, errorDto},
                {prefix + "if(true){" + statement + "}" + appendix, errorDto},
                {prefix + "while(true){" + statement + "}" + appendix, errorDto},
                {prefix + "for(;;){" + statement + "}" + appendix, errorDto},
                {prefix + "foreach([1,2] as object $v){" + statement + "}" + appendix, errorDto},
                {prefix + "try{" + statement + "}catch(\\Exception $e){}" + appendix, errorDto},
                {prefix + "try{}catch(\\Exception $e){" + statement + "}" + appendix, errorDto},
                //not all catch blocks return/throw
                {
                        prefix + "try{" + statement + "}"
                                + "catch(\\ErrorException $e){} catch(\\Exception $e2){" + statement + "}" + appendix,
                        errorDto
                },
                {
                        prefix + "try{" + statement + "}"
                                + "catch(\\ErrorException $e){" + statement + "} catch(\\Exception $e2){}" + appendix,
                        errorDto
                },
                //needs default case to be sure that it returns
                {prefix + "switch(1){case 1:" + statement + "}" + appendix, errorDto},
                //break before return/throw statement
                {prefix + "switch(1){case 1: break; " + statement + "}" + appendix, errorDto},
                {prefix + "switch(1){default: break; " + statement + "}" + appendix, errorDto},
                //not all cases return/throw
                {
                        prefix + "switch(1){case 1: break; default: " + statement + "}" +
                                appendix,
                        errorDto
                },
        });
    }
}
