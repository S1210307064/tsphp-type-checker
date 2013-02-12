/*
 * Copyright 2012 Robert Stoll <rstoll@tutteli.ch>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package ch.tutteli.tsphp.typechecker.test.utils;

import ch.tutteli.tsphp.typechecker.utils.MapHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Robert Stoll <rstoll@tutteli.ch>
 */
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
