package cn.visolink.system.menus.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.system.menus.dao.MenuMapper;
import cn.visolink.system.menus.dao.MenusDao;
import cn.visolink.system.menus.model.Menus;
import cn.visolink.system.menus.model.form.MenusForm;
import cn.visolink.system.menus.model.vo.MenusVO;
import cn.visolink.system.menus.service.MenusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.cess.CessException;
import io.cess.util.PropertyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * Menus服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MenusServiceImpl extends ServiceImpl<MenusDao, Menus> implements MenusService {

    @Override
    public Integer save(MenusForm record) {
        record.setCreateTime(new Date().toString());
        Menus data = this.convertDO(record);
        return baseMapper.insert(data);
    }

    @Override
    public Integer updateById(MenusForm record) {
        Menus data = this.convertDO(record);
        data.setEditTime(new Date());
        return baseMapper.updateById(data);
    }

    @Override
    public Integer deleteById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        return baseMapper.deleteById(id);
    }

    @Override
    public MenusVO selectById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        Menus data = baseMapper.selectById(id);
        MenusVO result = PropertyUtil.copy(data, MenusVO.class);
        return result;
    }

    @Override
    public Map selectAll(MenusForm record) {
        QueryWrapper<Menus> queryWrapper = new QueryWrapper<>();
        record.setIsDel(0);

        queryWrapper.setEntity(PropertyUtil.copy(record, Menus.class));

        List<Menus> list = baseMapper.selectList(queryWrapper);
        return this.convert(list);
    }
    @Autowired
    private MenuMapper menuMapper;
    @Override
    public PageInfo<Menus> getChildMenu(Map map) {

        int pageIndex = Integer.parseInt(map.get("pageIndex") + "");
        int pageSize = Integer.parseInt(map.get("pageSize") + "");
        PageHelper.startPage(pageIndex, pageSize);

        List<Menus> list = menuMapper.getChildMenu(map);
        PageInfo<Menus> pageInfo = new PageInfo<>(list);

        return pageInfo;
    }
    @Override
    public IPage<MenusVO> selectPage(MenusForm record) {
        // form -> do 转换
        Menus data = PropertyUtil.copy(record, Menus.class);
        // 分页数据设置
        Page<Menus> page = new Page<>(record.getCurrent(), record.getSize());
        // 查询条件
        QueryWrapper<Menus> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(data);
        IPage<Menus> list = baseMapper.selectPage(page, queryWrapper);
        IPage<MenusVO> iPage = new Page<>();
        iPage.setRecords(PropertyUtil.copy(list.getRecords(), MenusVO.class));
        iPage.setCurrent(list.getCurrent());
        iPage.setSize(list.getSize());
        iPage.setTotal(list.getTotal());
        iPage.setPages(list.getPages());
        return iPage;
    }

    @Override
    public void menusBatchesInsert(Map map) {
        String jsonMenus="[{\"name\":\"selfCanalCustomer\",\"path\":\"/\",\"hidden\":false,\"component\":\"Layout\",\"alwaysShow\":true,\"redirect\":\"/selfCanalCustomer\",\"meta\":{\"title\":\"客户\",\"icon\":\"/images/custom_normal.png\",\"active_icon\":\"/images/custom_active.png\",\"noCache\":true,\"showMenu\":true},\"children\":[{\"name\":\"内渠客户\",\"path\":\"selfCanalCustomer\",\"hidden\":false,\"component\":\"selfCanalCustomer\",\"alwaysShow\":true,\"meta\":{\"title\":\"客户\",\"icon\":\"/images/custom_normal.png\",\"active_icon\":\"/images/custom_active.png\",\"noCache\":true,\"showMenu\":true}},{\"name\":\"维护内渠客户标签\",\"path\":\"selfCanalCustomer/tags\",\"hidden\":true,\"component\":\"custom/tags\",\"alwaysShow\":false,\"meta\":{\"title\":\"维护客户标签\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠跟进记录\",\"path\":\"selfCanalCustomer/followUp\",\"hidden\":true,\"component\":\"custom/followUp\",\"alwaysShow\":false,\"meta\":{\"title\":\"跟进记录\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠新增跟进记录\",\"path\":\"selfCanalCustomer/followUp/addFollowUp\",\"hidden\":true,\"component\":\"custom/followUp/addFollowUp\",\"alwaysShow\":false,\"meta\":{\"title\":\"新增跟进记录\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠客户详情\",\"path\":\"selfCanalCustomer/detail/:id\",\"hidden\":true,\"component\":\"selfCanalCustomer/details\",\"alwaysShow\":false,\"meta\":{\"title\":\"客户详情\",\"icon\":\"\",\"noCache\":true},\"children\":[{\"name\":\"编辑内渠客户\",\"path\":\"edit\",\"hidden\":true,\"component\":\"selfCanalCustomer/details/edit\",\"alwaysShow\":false,\"meta\":{\"title\":\"编辑\",\"icon\":\"\",\"noCache\":true}}]}]},{\"name\":\"reportedselfCanalCustomer\",\"path\":\"/reportedselfCanalCustomer\",\"hidden\":false,\"component\":\"reportedselfCanalCustomer\",\"alwaysShow\":false,\"meta\":{\"title\":\"客户报备\",\"icon\":\"/images/filing_normal.png\",\"active_icon\":\"/images/filing_active.png\",\"noCache\":true}},{\"name\":\"selfCanalMessage\",\"path\":\"/selfCanalMessage\",\"hidden\":true,\"component\":\"selfCanalMessage\",\"alwaysShow\":false,\"redirect\":\"/selfCanalMessage/warning\",\"meta\":{\"title\":\"消息\",\"icon\":\"message\",\"noCache\":true},\"children\":[{\"name\":\"内渠预警\",\"path\":\"warning\",\"hidden\":true,\"component\":\"selfCanalMessage/warning\",\"alwaysShow\":false,\"meta\":{\"title\":\"消息\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠通知\",\"path\":\"notice\",\"hidden\":true,\"component\":\"selfCanalMessage/notice\",\"alwaysShow\":false,\"meta\":{\"title\":\"消息\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠报备失效预警\",\"path\":\"warning/invalidReported\",\"hidden\":true,\"component\":\"selfCanalMessage/item\",\"alwaysShow\":false,\"meta\":{\"title\":\"报备失效预警\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠渠道属性预警\",\"path\":\"warning/channelProperty\",\"hidden\":true,\"component\":\"selfCanalMessage/item\",\"alwaysShow\":false,\"meta\":{\"title\":\"渠道属性失效预警\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠报备失效通知\",\"path\":\"notice/invalidReported\",\"hidden\":true,\"component\":\"selfCanalMessage/item\",\"alwaysShow\":false,\"meta\":{\"title\":\"报备失效通知\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"内渠渠道属性通知\",\"path\":\"notice/channelProperty\",\"hidden\":true,\"component\":\"selfCanalMessage/item\",\"alwaysShow\":false,\"meta\":{\"title\":\"渠道属性失效通知\",\"icon\":\"\",\"noCache\":true}}]},{\"name\":\"person\",\"path\":\"/person\",\"hidden\":false,\"component\":\"Layout\",\"alwaysShow\":true,\"meta\":{\"title\":\"个人中心\",\"icon\":\"/images/person_normal.png\",\"active_icon\":\"/images/person_active.png\",\"noCache\":true,\"showMenu\":true},\"children\":[{\"name\":\"个人中心\",\"path\":\"\",\"hidden\":false,\"component\":\"person\",\"alwaysShow\":true,\"meta\":{\"title\":\"个人中心\",\"icon\":\"/images/person_normal.png\",\"active_icon\":\"/images/person_active.png\",\"noCache\":true,\"showMenu\":true}},{\"name\":\"下载中心\",\"path\":\"downloadUrl\",\"hidden\":true,\"component\":\"person/downloadUrl\",\"alwaysShow\":false,\"meta\":{\"title\":\"下载中心\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"切换项目岗位\",\"path\":\"togglePost\",\"hidden\":true,\"component\":\"person/togglePost\",\"alwaysShow\":false,\"meta\":{\"title\":\"切换项目岗位\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"意见反馈\",\"path\":\"feedBack\",\"hidden\":true,\"component\":\"person/feedBack\",\"alwaysShow\":false,\"meta\":{\"title\":\"意见反馈\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"个人业绩\",\"path\":\"performance\",\"hidden\":true,\"component\":\"person/performance\",\"alwaysShow\":false,\"meta\":{\"title\":\"个人业绩\",\"icon\":\"\",\"noCache\":true}},{\"name\":\"扫码签到\",\"path\":\"scanSignIn\",\"hidden\":true,\"component\":\"person/scanSignIn\",\"alwaysShow\":false,\"meta\":{\"title\":\"扫码签到\",\"icon\":\"\",\"noCache\":true}}]}]";
        JSONArray jsonArray = JSONUtil.parseArray(jsonMenus);
        for (Object o : jsonArray) {
            Map<String , Object> objectObjectHashMap = MapUtil.newHashMap();
            String uuId = RandomUtil.randomUUID();
            JSONObject jsonObject = (JSONObject) o;
            JSONObject meta = (JSONObject) jsonObject.get("meta");
            String meatString = meta.toJSONString(4);
            JSONArray children = (JSONArray) jsonObject.get("children")==null ?  new JSONArray()  : (JSONArray) jsonObject.get("children");
            objectObjectHashMap.put("uuId",uuId);
            objectObjectHashMap.put("pId","-1");
            objectObjectHashMap.put("MenuName",jsonObject.get("name"));
            objectObjectHashMap.put("Url",jsonObject.get("path"));
            objectObjectHashMap.put("ImageUrl","");
            objectObjectHashMap.put("IsHomePage",0);
            objectObjectHashMap.put("Levels",1);
            objectObjectHashMap.put("ListIndex",0);
            String path = "" + jsonObject.get("name");
            objectObjectHashMap.put("FullPath",path);
            objectObjectHashMap.put("Creator","autoJob");
            objectObjectHashMap.put("CreateTime", DateUtil.formatDateTime(new Date()));
            objectObjectHashMap.put("Editor","autoJob");
            objectObjectHashMap.put("EditTime", DateUtil.formatDateTime(new Date()));
            objectObjectHashMap.put("MenuSysName",jsonObject.get("name"));
            objectObjectHashMap.put("IconClass","");
            objectObjectHashMap.put("component",jsonObject.get("component"));
            objectObjectHashMap.put("alwaysShow",jsonObject.get("alwaysShow"));
            objectObjectHashMap.put("meta",meatString);
            objectObjectHashMap.put("redirect",jsonObject.get("redirect"));

            this.baseMapper.menusBatchesInsert(objectObjectHashMap);
            for (Object child : children) {
                JSONObject childNode = (JSONObject) child;
                JSONObject childmeta = (JSONObject) childNode.get("meta");
                String childmeatString = childmeta.toJSONString(4);
                JSONArray childchildren = (JSONArray) childNode.get("children")== null ?  new JSONArray()  : (JSONArray) childNode.get("children");
                String uuId2 =  RandomUtil.randomUUID();
                String path2 = path +"/" + childNode.get("name");
                objectObjectHashMap= MapUtil.newHashMap();
                objectObjectHashMap.put("uuId",uuId2);
                objectObjectHashMap.put("pId",uuId);
                objectObjectHashMap.put("MenuName",childNode.get("name"));
                objectObjectHashMap.put("Url",childNode.get("path"));
                objectObjectHashMap.put("ImageUrl","");
                objectObjectHashMap.put("IsHomePage",0);
                objectObjectHashMap.put("Levels",2);
                objectObjectHashMap.put("ListIndex",0);
                objectObjectHashMap.put("FullPath",path2);
                objectObjectHashMap.put("Creator","autoJob");
                objectObjectHashMap.put("CreateTime", DateUtil.formatDateTime(new Date()));
                objectObjectHashMap.put("Editor","autoJob");
                objectObjectHashMap.put("EditTime", DateUtil.formatDateTime(new Date()));
                objectObjectHashMap.put("MenuSysName",childNode.get("name"));
                objectObjectHashMap.put("IconClass","");
                objectObjectHashMap.put("component",childNode.get("component"));
                objectObjectHashMap.put("alwaysShow",childNode.get("alwaysShow"));
                objectObjectHashMap.put("meta",childmeatString);
                objectObjectHashMap.put("redirect",childNode.get("redirect"));
                this.baseMapper.menusBatchesInsert(objectObjectHashMap);

                for (Object childchild : childchildren) {
                    JSONObject childNode3 = (JSONObject) childchild;
                    JSONObject childmeta3 = (JSONObject) childNode3.get("meta");
                    String childmeatString3 = childmeta3.toJSONString(4);
                    JSONArray childchildren3= (JSONArray) childNode3.get("children")==null ? new JSONArray() : (JSONArray) childNode3.get("children");

                    String uuId3 =  RandomUtil.randomUUID();
                    String path3 = path2 +"/" + childNode3.get("name");
                    objectObjectHashMap= MapUtil.newHashMap();
                    objectObjectHashMap.put("uuId",uuId3);
                    objectObjectHashMap.put("pId",uuId2);
                    objectObjectHashMap.put("MenuName",childNode3.get("name"));
                    objectObjectHashMap.put("Url",childNode3.get("path"));
                    objectObjectHashMap.put("ImageUrl","");
                    objectObjectHashMap.put("IsHomePage",0);
                    objectObjectHashMap.put("Levels",3);
                    objectObjectHashMap.put("ListIndex",0);
                    objectObjectHashMap.put("FullPath",path3);
                    objectObjectHashMap.put("Creator","autoJob");
                    objectObjectHashMap.put("CreateTime", DateUtil.formatDateTime(new Date()));
                    objectObjectHashMap.put("Editor","autoJob");
                    objectObjectHashMap.put("EditTime", DateUtil.formatDateTime(new Date()));
                    objectObjectHashMap.put("MenuSysName",childNode3.get("name"));
                    objectObjectHashMap.put("IconClass","");
                    objectObjectHashMap.put("component",childNode3.get("component"));
                    objectObjectHashMap.put("alwaysShow",childNode3.get("alwaysShow"));
                    objectObjectHashMap.put("meta",childmeatString3);
                    objectObjectHashMap.put("redirect",childNode3.get("redirect"));
                    this.baseMapper.menusBatchesInsert(objectObjectHashMap);

                    for (Object o1 : childchildren3) {
                        JSONObject childNode4= (JSONObject) o1;
                        JSONObject childmeta4 = (JSONObject) childNode4.get("meta");
                        String childmeatString4 = childmeta4.toJSONString(4);
                        JSONArray childchildren4= (JSONArray) childNode4.get("children")==null ? new JSONArray() : (JSONArray) childNode4.get("children");

                        String uuId4 =  RandomUtil.randomUUID();
                        String path4 = path3 +"/" + childNode4.get("name");
                        objectObjectHashMap= MapUtil.newHashMap();
                        objectObjectHashMap.put("uuId",uuId4);
                        objectObjectHashMap.put("pId",uuId3);
                        objectObjectHashMap.put("MenuName",childNode4.get("name"));
                        objectObjectHashMap.put("Url",childNode4.get("path"));
                        objectObjectHashMap.put("ImageUrl","");
                        objectObjectHashMap.put("IsHomePage",0);
                        objectObjectHashMap.put("Levels",4);
                        objectObjectHashMap.put("ListIndex",0);
                        objectObjectHashMap.put("FullPath",path4);
                        objectObjectHashMap.put("Creator","autoJob");
                        objectObjectHashMap.put("CreateTime", DateUtil.formatDateTime(new Date()));
                        objectObjectHashMap.put("Editor","autoJob");
                        objectObjectHashMap.put("EditTime", DateUtil.formatDateTime(new Date()));
                        objectObjectHashMap.put("MenuSysName",childNode4.get("name"));
                        objectObjectHashMap.put("IconClass","");
                        objectObjectHashMap.put("component",childNode4.get("component"));
                        objectObjectHashMap.put("alwaysShow",childNode4.get("alwaysShow"));
                        objectObjectHashMap.put("meta",childmeatString4);
                        objectObjectHashMap.put("redirect",childNode4.get("redirect"));
                        this.baseMapper.menusBatchesInsert(objectObjectHashMap);

                    }
                }
            }
            System.out.println();
        }
//        this.baseMapper.menusBatchesInsert();
    }


    /**
     * Form -> Do
     *
     * @param form 对象
     * @return Do对象
     */
    private Menus convertDO(MenusForm form) {
        Menus data = new Menus();
        data.setId(form.getId());
        data.setPid(form.getPid());
        data.setMenuName(form.getMenuName());
        data.setUrl(form.getUrl());
        data.setImageUrl(form.getImageUrl());
        data.setIsHomePage(form.getIsHomePage());
        data.setIsShow(form.getIsShow());
        data.setLevels(form.getLevels());
        data.setListIndex(form.getListIndex());
        data.setFullPath(form.getFullPath());
        data.setIsLast(form.getIsLast());
        data.setCreator(form.getCreator());
        data.setCreateTime(DateUtil.parseTime(form.getCreateTime()));
        data.setEditor(form.getEditor());
        data.setEditTime(DateUtil.parseTime(form.getEditTime()));
        data.setStatus(form.getStatus());
        data.setIsDel(form.getIsDel());
        data.setMenuSysName(form.getMenuSysName());
        data.setIconClass(form.getIconClass());
        data.setMenusType(form.getMenusType());
        data.setRemarks(form.getRemarks());
        return data;
    }

    /**
     * Do -> VO
     *
     * @param list 对象
     * @return VO对象
     */
    private Map convert(List<Menus> list) {
        List<MenusVO> menusList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return MapUtil.newHashMap();
        }
        for (Menus source : list) {
            MenusVO target = new MenusVO();
            BeanUtils.copyProperties(source, target);
            menusList.add(target);
        }
        Map map = this.buildTree(menusList);
        return map;
    }

    /**
     * 构建 orgTree
     * @param menusList
     * @return
     */
    public  Map buildTree(List<MenusVO> menusList) {
        List<MenusVO> trees = CollUtil.newArrayList();
        for (MenusVO vo : menusList) {

            if ("-1".equals(vo.getPid())) {
                trees.add(vo);
            }
            for (MenusVO organizationVO : menusList) {
                if (organizationVO.getPid().equals(vo.getId())) {
                    if (vo.getChildren() == null) {
                        vo.setChildren(new ArrayList<MenusVO>());
                    }
                    vo.getChildren().add(organizationVO);
                }

            }
        }
        Map map = MapUtil.newHashMap();
        map.put("content",trees.size() == 0?menusList:trees);
        map.put("totalElements",menusList!=null?menusList.size():0);
        return map;
    }
}
