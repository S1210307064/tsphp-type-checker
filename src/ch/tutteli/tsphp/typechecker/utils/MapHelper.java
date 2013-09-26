package ch.tutteli.tsphp.typechecker.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MapHelper
{

    private MapHelper() {
    }

    public static <TKey, TValue> void addToListMap(Map<TKey, List<TValue>> map, TKey key, TValue value) {
        if (map.containsKey(key)) {
            map.get(key).add(value);
        } else {
            List<TValue> list = new ArrayList<>();
            list.add(value);
            map.put(key, list);
        }
    }
}
