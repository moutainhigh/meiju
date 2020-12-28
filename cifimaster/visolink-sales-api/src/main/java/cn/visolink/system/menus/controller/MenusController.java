package cn.visolink.system.menus.controller;

import cn.visolink.logs.aop.log.Log;
import cn.visolink.system.menus.model.form.MenusForm;
import cn.visolink.system.menus.model.vo.MenusVO;
import cn.visolink.system.menus.service.MenuBiz;
import cn.visolink.system.menus.service.MenusService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.pagehelper.PageInfo;
import io.cess.core.spring.CessBody;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * Menus前端控制器
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
@RestController
@Api(tags = "系统管理-菜单管理")
@RequestMapping("/menus")
public class MenusController {

    @Autowired
    public MenusService menusService;

    @Autowired
    private MenuBiz menuBiz;

    /**
     * 保存单条
     * @return 是否添加成功
     */
    @Log("保存数据到Menus")
    @CessBody
    @ApiOperation(value = "保存", notes = "保存数据到Menus")
    @PostMapping(value = "/add.action")
    public Integer addMenus(@RequestBody Map map) {
        if(map.get("pid")==null){
            map.put("pid","-1");
        }
        if("1".equals(map.get("menusType"))){
            map.put("menusType",1);
            map.put("IsShow",1);
        }
        //如果新建菜单为2--功能 默认isShow=0
        if("2".equals(map.get("menusType"))){
            map.put("menusType",2);
            map.put("IsShow",0);
        }

        return menuBiz.addMenu(map);
    }

    @Log("更新菜单")
    @CessBody
    @ApiOperation(value = "保存", notes = "保存数据到Menus")
    @PostMapping(value = "/updateMenu.action")
    public Integer updateMenu(@RequestBody Map map) {
        if("启用".equals(map.get("Status"))){
            map.put("Status",1);
        }
        if("禁用".equals(map.get("Status"))){
            map.put("Status",0);
        }
        if("菜单".equals(map.get("menusType"))){
            map.put("menusType",1);
        }
        if("功能".equals(map.get("menusType"))){
            map.put("menusType",2);
        }
        return menuBiz.updateMenu(map);
    }
    /**
     * 更新(根据主键id更新)
     ** @return 是否更改成功
     */
    @Log("更新(根据主键id更新)Menus")
    @CessBody
    @ApiOperation(value = "更新数据", notes = "根据主键id更新Menus数据")
    @PostMapping(value = "/updateMenuStatus")
    public Integer updateMenuStatus(@RequestBody Map map) {
        return menuBiz.updateMenuStatus(map);
    }

    /**
     * 删除(根据主键id伪删除)
     *
     * @param id 主键id
     * @return 是否删除成功
     */
    @Log("删除(根据主键id伪删除)Menus")
    @CessBody
    @ApiOperation(value = "删除数据", notes = "根据主键id伪删除Menus数据")
    @PostMapping(value = "/deleteById.action")
    public Integer deleteMenusById(String id) {
        Integer result = menusService.deleteById(id);
        return result;
    }

    /**
     * 根据主键id查询单条
     *
     * @param id 主键id
     * @return 查询结果
     */
    @Log("根据主键id查询单条Menus")
    @CessBody
    @ApiOperation(value = "获取单条数据", notes = "根据主键id获取Menus数据")
    @RequestMapping(value = "/getById.action", method = RequestMethod.POST)
    public MenusVO getMenusById(@RequestBody(required = false) String id) {
        MenusVO result = menusService.selectById(id);
        return result;
    }
    /**
     * 查询全部
     *
     * @param param 查询条件
     * @return 查询结果
     */
    @Log("查询全部Menus")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "查询Menus全部数据")
    @RequestMapping(value = "/queryAll.action", method = RequestMethod.POST)
    public Map getMenusAll( MenusForm param) {
        Map result = menusService.selectAll(param);
        return result;
    }
    /**
     * 查询子菜单
     *
     * @return 查询结果
     */
    @Log("查询子菜单")
    @CessBody
    @ApiOperation(value = "全部查询", notes = "查询Menus全部数据")
    @RequestMapping(value = "/queryChildMenus.action", method = RequestMethod.POST)
    public PageInfo getChildMenu(@RequestBody Map  map) {
        PageInfo childMenu = menusService.getChildMenu(map);
        return childMenu;
    }

    /**
     * 分页查询
     *
     * @param param 查询条件
     * @return 查询结果
     */
    @Log("分页查询Menus")
    @CessBody
    @ApiOperation(value = "分页查询", notes = "分页查询Menus全部数据")
    @RequestMapping(value = "/queryPage.action", method = RequestMethod.POST)
    public IPage<MenusVO> getMenusPage(@RequestBody(required = false) MenusForm param) {
//        menusService.menusBatchesInsert(MapUtil.newHashMap());
        IPage<MenusVO> result = menusService.selectPage(param);
        return result;
    }

}

