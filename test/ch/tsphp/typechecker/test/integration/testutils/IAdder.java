package ch.tsphp.typechecker.test.integration.testutils;

import java.util.SortedSet;

public interface IAdder
{

    void add(String type, String typeExpected, SortedSet<Integer> modifiers);
}
