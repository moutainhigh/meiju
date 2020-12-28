package cn.visolink.system.menus.service;

import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */

public interface MenuBiz {

    /**
     * 添加菜单
     * */
    int addMenu(Map map);
    /**
     * 编辑菜单
     * */
    int updateMenu(Map map);

    /**
     * 删除菜单
     * */
    int updateMenuStatus(Map map);
}
