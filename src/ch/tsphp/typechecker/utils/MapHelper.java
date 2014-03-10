/*
 * This file is part of the TSPHP project published under the Apache License 2.0
 * For the full copyright and license information, please have a look at LICENSE in the
 * root folder or visit the project's website http://tsphp.ch/wiki/display/TSPHP/License
 */

package ch.tsphp.typechecker.utils;

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
