package cn.visolink.system.menus.dao;

import cn.visolink.system.menus.model.Menus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
public interface MenusDao extends BaseMapper<Menus> {
    /**
     * 批量新增菜单，用于初始化菜单
     * @param map
     */
    public  void  menusBatchesInsert(Map map);
    public List queryAllMenus();
}
