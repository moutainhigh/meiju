package cn.visolink.system.menus.service;

import cn.visolink.system.menus.model.Menus;
import cn.visolink.system.menus.model.form.MenusForm;
import cn.visolink.system.menus.model.vo.MenusVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;

import java.util.Map;

/**
 * <p>
 * Menus服务类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
public interface MenusService extends IService<Menus> {
    /**
     * 保存信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    Integer save(MenusForm record);

    /**
     * 根据主键更新信息对象
     *
     * @param record 信息对象
     * @return 影响记录数
     */
    Integer updateById(MenusForm record);
    /**
     * 根据主键删除信息对象
     * 逻辑删除,字段改为删除态
     *
     * @param id 主键
     * @return 影响记录数
     */
    Integer deleteById(String id);

    /**
     * 根据主键查询信息对象
     *
     * @param id 主键
     * @return 信息对象
     */
    MenusVO selectById(String id);

    /**
     * 根据主键查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    Map selectAll(MenusForm record);
    PageInfo getChildMenu(Map map);

    /**
     * 分页查询信息对象
     *
     * @param record 查询请求条件
     * @return 信息列表
     */
    IPage<MenusVO> selectPage(MenusForm record);

    /**
     * 批量新增菜单用于初始化菜单
     * @param map
     */
    public  void  menusBatchesInsert(Map map);

}
