package cn.visolink.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/14 2:30 下午
 */
public class TreeUtils {
    public static Map buildTree(List<Map> menuList) {

        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps =null;
        for (Map menu : menuList) {
            if ("-1".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            childrenMaps= new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    childrenMaps.add(it);
                }
            }
            menu.put("childMap",childrenMaps);
        }

        Map map = MapUtil.newHashMap();
        map.put("content",trees.size() == 0?menuList:trees);
        return map;
    }


}
