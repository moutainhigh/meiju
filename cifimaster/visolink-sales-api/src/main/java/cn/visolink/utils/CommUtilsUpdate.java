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
public class CommUtilsUpdate {
    public static Map buildTwoTree(List<Map> menuList) {
        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps =null;
        List<Map> child =null;
        for (Map menu : menuList) {

            if ("-1".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            childrenMaps= new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    childrenMaps.add(it);

                    child= new ArrayList<>();
                    for (Map it2 : menuList) {
                        if (it2.get("PPID").equals(it.get("ID"))) {
                            child.add(it2);
                        }
                    }
                    it.put("child",child);
                }
            }

            menu.put("children",childrenMaps);

        }
        Map map = MapUtil.newHashMap();
        map.put("result",trees.size() == 0?menuList:trees);
        map.put("totalElements",menuList!=null?menuList.size():0);
        map.put("code",200);
        return map;
    }


    public static Map buildTree(List<Map> menuList) {
        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps =null;
        List<Map> child =null;
        for (Map menu : menuList) {

            if ("-1".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            childrenMaps= new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    childrenMaps.add(it);

                    child= new ArrayList<>();
                    for (Map it2 : menuList) {
                        if (it2.get("PPID").equals(it.get("ID"))) {
                            child.add(it2);
                        }
                    }
                    it.put("child",child);
                }
            }

            menu.put("children",childrenMaps);

        }
        Map map = MapUtil.newHashMap();
        map.put("result",trees.size() == 0?menuList:trees);
        map.put("totalElements",menuList!=null?menuList.size():0);
        map.put("code",200);
        return map;
    }
    public static Map buildTreeFour(List<Map> menuList,List<Map> orgId) {
        List<Map> trees = CollUtil.newArrayList();
        List<Map> childrenMaps =null;
        List<Map> child =null;
        List<Map> childFour =null;
        for (Map menu : menuList) {

            if ("-1".equals(menu.get("PID").toString())) {
                trees.add(menu);
            }
            if(orgId.size()>0){
                for (int i = 0; i < orgId.size(); i++) {
                    if(orgId.get(i).get("orgId").equals(menu.get("ID"))){
                        menu.put("isClick","true");
                    }
                }
            }

            childrenMaps= new ArrayList<>();
            for (Map it : menuList) {
                if (it.get("PID").equals(menu.get("ID"))) {
                    if(menu.get("isClick")!=null&&menu.get("isClick").equals("true")){
                        it.put("isClick","true");
                    }else{
                        if(orgId.size()>0){
                            for (int i = 0; i < orgId.size(); i++) {
                                if(orgId.get(i).get("orgId").equals(it.get("ID"))){
                                    it.put("isClick","true");
                                }
                            }
                        }
                    }

                    childrenMaps.add(it);

                    child= new ArrayList<>();
                    for (Map it2 : menuList) {
                        if (it2.get("PPID").equals(it.get("ID"))) {
                            if(it.get("isClick")!=null&&it.get("isClick").equals("true")){
                                it2.put("isClick","true");
                            }else{
                                if(orgId.size()>0){
                                    for (int i = 0; i < orgId.size(); i++) {
                                        if(orgId.get(i).get("orgId").equals(it2.get("ID"))){
                                            it2.put("isClick","true");
                                        }
                                    }
                                }
                            }
                            child.add(it2);
                            childFour= new ArrayList<>();
                            for (Map it3 : menuList) {
                                if (it3.get("PPPID").equals(it2.get("ID"))) {
                                        it3.put("isClick","true");
                                    childFour.add(it3);
                                }
                            }
                            it2.put("child",childFour);
                        }
                    }
                    it.put("child",child);
                }
            }

            menu.put("children",childrenMaps);

        }
        Map map = MapUtil.newHashMap();
        map.put("result",trees.size() == 0?menuList:trees);
        map.put("totalElements",menuList!=null?menuList.size():0);
        map.put("code",200);
        return map;
    }
}
