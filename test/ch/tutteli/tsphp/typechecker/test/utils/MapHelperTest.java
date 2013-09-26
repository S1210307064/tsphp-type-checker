package ch.tutteli.tsphp.typechecker.test.utils;

import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

public class MapHelperTest
{

    @Test
    public void test() {
        Map<String, List<String>> map = new HashMap<>();
        Assert.assertEquals(0, map.size());

        List<String> list = new ArrayList<>();
        list.add("v");
        MapHelper.addToListMap(map, "k", "v");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(list, map.get("k"));

        list.add("v2");
        MapHelper.addToListMap(map, "k", "v2");
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(list, map.get("k"));

        List<String> list2 = new ArrayList<>();
        list2.add("a");
        MapHelper.addToListMap(map, "x", "a");
        Assert.assertEquals(2, map.size());
        Assert.assertEquals(list, map.get("k"));
        Assert.assertEquals(list2, map.get("x"));
    }
}
