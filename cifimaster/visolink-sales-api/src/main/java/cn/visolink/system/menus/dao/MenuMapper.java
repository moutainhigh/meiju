package cn.visolink.system.menus.dao;

import cn.visolink.system.menus.model.Menus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.26
 */
@Mapper
public interface MenuMapper {


    /**
     * 添加菜单
     * */
    int addMenu(Map map);
    List<Menus> getChildMenu(Map map);

    /**
     * 编辑菜单
     * */
    int updateMenu(Map map);

    int updateMenuStatus(Map map);
}
