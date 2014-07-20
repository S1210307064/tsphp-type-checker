/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.test.integration.testutils;

import ch.tsphp.typechecker.antlr.TSPHPDefinitionWalker;
import ch.tsphp.typechecker.symbols.ModifierHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class ParameterListHelper
{

    private static String prefix;
    private static String appendix;
    private static String prefixExpected;
    private static String scopeName;
    private static boolean isDefinitionPhase;

    private ParameterListHelper() {
    }

    public static Collection<Object[]> getTestStrings(final String thePrefix, final String theAppendix,
            final String thePrefixExpected, final String theScopeName,
            final boolean isItDefinitionPhase) {

        prefix = thePrefix;
        appendix = theAppendix;
        prefixExpected = thePrefixExpected;
        scopeName = theScopeName;
        isDefinitionPhase = isItDefinitionPhase;

        //check all types
        final List<Object[]> collection = new ArrayList<>();
        TypeHelper.getAllTypesInclModifier(new IAdder()
        {
            @Override
            public void add(String type, String typeExpected, SortedSet<Integer> modifiers) {
                String typeExpected2 = isDefinitionPhase ? "" : typeExpected;

                String typeModifiers = ModifierHelper.getModifiers(modifiers);
                collection.add(new Object[]{
                        prefix + type + "$a" + appendix,
                        prefixExpected + scopeName + typeExpected + " "
                                + scopeName + "$a" + typeExpected2 + typeModifiers
                });
                collection.add(new Object[]{
                        prefix + type + "$a=1" + appendix,
                        prefixExpected + scopeName + typeExpected + " "
                                + scopeName + "$a" + typeExpected2 + typeModifiers
                });
            }
        });

        int qMark = TSPHPDefinitionWalker.QuestionMark;
        int cast = TSPHPDefinitionWalker.Cast;

        //all types can have the ? modifier not only scalars for parameters
        List<String> types = TypeHelper.getAllTypesWithoutScalar();
        for (String type : types) {
            String typeExpected = isDefinitionPhase ? "" : type;
            collection.add(new Object[]{
                    prefix + type + "? $a" + appendix,
                    prefixExpected + scopeName + type + " "
                            + scopeName + "$a" + typeExpected + "|" + qMark
            });
        }

        //normal
        collection.addAll(getVariations("bool", "bool", ""));
        //cast 
        collection.addAll(getVariations("cast int", "int", "|" + cast));
        //?
        collection.addAll(getVariations("float?", "float", "|" + qMark));

        //cast and ? mixed
        String typeModifierExpected = ModifierHelper.getModifiers(
                new TreeSet<>(Arrays.asList(new Integer[]{cast, qMark})));
        collection.addAll(getVariations("cast string?", "string", typeModifierExpected));

        collection.addAll(getVariationsForOptional());

        return collection;
    }

    private static Collection<Object[]> getVariations(String type, String typeExpected, String typeModifierExpected) {


        String dynPrefix = scopeName + typeExpected + " " + scopeName;
        String dynAppendix = (isDefinitionPhase ? "" : typeExpected) + typeModifierExpected;

        String paramStat1 = "int $x";
        String paramStat2 = "int $y";
        String paramStat1Expected = scopeName + "int " + scopeName + "$x" + (isDefinitionPhase ? "" : "int");
        String paramStat2Expected = scopeName + "int " + scopeName + "$y" + (isDefinitionPhase ? "" : "int");

        return Arrays.asList(new Object[][]{
                {
                        prefix + type + " $a" + appendix,
                        prefixExpected + dynPrefix + "$a" + dynAppendix
                },
                {
                        prefix + type + " $a" + "," + paramStat1 + appendix,
                        prefixExpected
                                + dynPrefix + "$a" + dynAppendix + " "
                                + paramStat1Expected
                },
                {
                        prefix + paramStat1 + "," + type + " $a" + appendix,
                        prefixExpected
                                + paramStat1Expected + " "
                                + dynPrefix + "$a" + dynAppendix
                },
                {
                        prefix + type + " $a" + ", " + paramStat1 + ", " + paramStat2 + appendix,
                        prefixExpected
                                + dynPrefix + "$a" + dynAppendix + " "
                                + paramStat1Expected + " "
                                + paramStat2Expected
                },
                {
                        prefix + type + " $a" + ", " + type + " $b" + ", " + paramStat1 + appendix,
                        prefixExpected
                                + dynPrefix + "$a" + dynAppendix + " "
                                + dynPrefix + "$b" + dynAppendix + " "
                                + paramStat1Expected
                },
                {
                        prefix + type + " $a" + ", " + paramStat1 + "," + type + " $b" + "" + appendix,
                        prefixExpected
                                + dynPrefix + "$a" + dynAppendix + " "
                                + paramStat1Expected + " "
                                + dynPrefix + "$b" + dynAppendix
                },
                {
                        prefix + paramStat1 + "," + type + " $a" + ", " + paramStat2 + appendix,
                        prefixExpected
                                + paramStat1Expected + " "
                                + dynPrefix + "$a" + dynAppendix + " "
                                + paramStat2Expected
                },
                {
                        prefix + paramStat1 + "," + type + " $a" + ", " + type + " $b" + "" + appendix,
                        prefixExpected
                                + paramStat1Expected + " "
                                + dynPrefix + "$a" + dynAppendix + " "
                                + dynPrefix + "$b" + dynAppendix
                },
                {
                        prefix + type + " $a, " + type + " $b , " + type + " $c" + appendix,
                        prefixExpected
                                + dynPrefix + "$a" + dynAppendix + " "
                                + dynPrefix + "$b" + dynAppendix + " "
                                + dynPrefix + "$c" + dynAppendix
                }
        });
    }

    private static Collection<Object[]> getVariationsForOptional() {

        String typeExpected = isDefinitionPhase ? "" : "int";
        int qMark = TSPHPDefinitionWalker.QuestionMark;
        int cast = TSPHPDefinitionWalker.Cast;
        String typeModifiersCastAndNullable = ModifierHelper.getModifiers(
                new TreeSet<>(Arrays.asList(new Integer[]{cast, qMark})));

        String a = prefixExpected + scopeName + "int " + scopeName + "$a" + typeExpected;
        String b = scopeName + "int " + scopeName + "$b" + typeExpected;
        String c = scopeName + "int " + scopeName + "$c" + typeExpected;
        String d = scopeName + "int " + scopeName + "$d" + typeExpected;

        List<Object[]> collection = new ArrayList<>();
        collection.addAll(Arrays.asList(new Object[][]{
                //optional parameter
                {
                        prefix + "int $a, int $b='hallo'" + appendix,
                        a + " " + b
                },
                {
                        prefix + "int $a, int? $b, int $c=+1" + appendix,
                        a + " "
                                + b + "|" + qMark + " "
                                + c
                },
                {
                        prefix + "int $a,cast int? $b, int $c=-10, int $d=2.0" + appendix,
                        a + " "
                                + b + typeModifiersCastAndNullable + " "
                                + c + " "
                                + d
                },
                {
                        prefix + "int? $a=null,int $b=true, int $c=E_ALL" + appendix,
                        a + "|" + qMark + " "
                                + b + " "
                                + c
                },
                {
                        prefix + "int $a, int $b=false, int $c=null" + appendix,
                        a + " "
                                + b + " "
                                + c
                },
                {
                        prefix + "int $a, int $b, int $c=true" + appendix,
                        a + " "
                                + b + " "
                                + c
                },
                {
                        prefix + "cast int $a=1, int? $b=2, cast int $c=3" + appendix,
                        a + "|" + cast + " "
                                + b + "|" + qMark + " "
                                + c + "|" + cast
                }
        }));

        String[] types = TypeHelper.getClassInterfaceTypes();

        for (String type : types) {
            collection.add(new Object[]{prefix + "int $a=" + type + "::a" + appendix, a});
        }
        return collection;
    }
}