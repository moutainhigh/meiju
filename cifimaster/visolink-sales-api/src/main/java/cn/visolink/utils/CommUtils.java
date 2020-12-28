package cn.visolink.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wcl
 * @version 1.0
 * @date 2019/9/4 7:28 下午
 */
public class CommUtils {
    public static Map buildTree(List<Map> menuList) {

        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps = null;
        for (Map menu : menuList) {

            if ("-1".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            childrenMaps = new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    childrenMaps.add(it);
                }
            }
            menu.put("children", childrenMaps);
        }
        Map map = MapUtil.newHashMap();
        map.put("content", trees.size() == 0 ? menuList : trees);
        map.put("totalElements", menuList != null ? menuList.size() : 0);
        return map;
    }

    static char[] cnArr = new char [] {'一','二','三','四','五','六','七','八','九'};
    /**
     * 将数字转换为中文数字， 这里只写到了万
     * @param intInput
     * @return
     */
    public static String arabicNumToChineseNum(int intInput) {

        String si = String.valueOf(intInput);
        String sd = "";
        if (si.length() == 1) {
            if (intInput == 0) {
                return sd;
            }
            sd += cnArr[intInput - 1];
            return sd;
        } else if (si.length() == 2) {
            if (si.substring(0, 1).equals("1")) {
                sd += "十";
                if (intInput % 10 == 0) {
                    return sd;
                }
            }else{
                sd += (cnArr[intInput / 10 - 1] + "十");
            }
            sd += arabicNumToChineseNum(intInput % 10);
        } else if (si.length() == 3) {
            sd += (cnArr[intInput / 100 - 1] + "百");
            if (String.valueOf(intInput % 100).length() < 2) {
                if (intInput % 100 == 0) {
                    return sd;
                }
                sd += "零";
            }
            sd += arabicNumToChineseNum(intInput % 100);
        } else if (si.length() == 4) {
            sd += (cnArr[intInput / 1000 - 1] + "千");
            if (String.valueOf(intInput % 1000).length() < 3) {
                if (intInput % 1000 == 0) {
                    return sd;
                }
                sd += "零";
            }
            sd += arabicNumToChineseNum(intInput % 1000);
        } else if (si.length() == 5) {
            sd += (cnArr[intInput / 10000 - 1] + "万");
            if (String.valueOf(intInput % 10000).length() < 4) {
                if (intInput % 10000 == 0) {
                    return sd;
                }
                sd += "零";
            }
            sd += arabicNumToChineseNum(intInput % 10000);
        }

        return sd;
    }
 


}
