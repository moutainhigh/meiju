package cn.visolink.system.menus.service.impl;

import cn.visolink.system.menus.dao.MenuMapper;
import cn.visolink.system.menus.service.MenuBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
@Service
public class MenuBizImpl implements MenuBiz {

    @Autowired
    private MenuMapper menuMapper;

    /**
     * 添加菜单
     * */


    @Override
    public int addMenu(Map map) {
        return menuMapper.addMenu(map);
    }

    /**
     * 编辑菜单
     * */
    @Override
    public int updateMenu(Map map) {
        return menuMapper.updateMenu(map);
    }

    /**
     * 删除菜单
     * */
    @Override
    public int updateMenuStatus(Map map) {
        return menuMapper.updateMenuStatus(map);
    }
}
