package cn.visolink.common.menu.service;

import java.util.List;
import java.util.Map;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/18 14:11
 * @Version 1.0
 **/
public interface MenuService {
    /**
     * 根据菜单父级Id和UserId获取菜单列表
     * @param UserId
     * @param pId
     * @return
     */
    List<Map> getMenusListByUserIdAndPid(String userId,String pId,String JobID);
}
