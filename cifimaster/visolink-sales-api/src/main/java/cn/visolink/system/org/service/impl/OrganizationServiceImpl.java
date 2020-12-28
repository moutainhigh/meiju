package cn.visolink.system.org.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.visolink.common.security.dao.AuthMapper;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.BaseResultCodeEnum;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.org.dao.OrganizationDao;
import cn.visolink.system.org.model.Organization;
import cn.visolink.system.org.model.form.OrganizationForm;
import cn.visolink.system.org.model.vo.OrganizationVO;
import cn.visolink.system.org.service.OrganizationService;
import cn.visolink.utils.CommUtils;
import cn.visolink.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.cess.CessException;
import io.cess.util.PropertyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;


/**
 * <p>
 * Organization服务实现类
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Service
public class OrganizationServiceImpl extends ServiceImpl<OrganizationDao, Organization> implements OrganizationService {

    @Autowired
    private AuthMapper authMapper;

    @Override
    public OrganizationForm save(OrganizationForm record) {
        if (ObjectUtil.isNull(record.getLevels())) {
            throw new BadRequestException(-10_0012, "参数格式不正确！");
        }
        if (StrUtil.isEmpty(record.getFullPath())) {
            throw new BadRequestException(-10_0012, "参数格式不正确！");
        }
        if (StrUtil.isEmpty(record.getOrgName())) {
            throw new BadRequestException(-10_0012, "参数格式不正确！");
        }
        Organization data = this.convertDO(record);
        data.setCreateTime(new Date());
        data.setOrgType(2);
        data.setLevels(data.getLevels() + 1);
        data.setFullPath(data.getFullPath() + "/" + data.getOrgName());
        data.setAuthCompanyID("ede1b679-3546-11e7-a3f8-5254007b6f02");
        data.setProductId("ee3b2466-3546-11e7-a3f8-5254007b6f02");
        //查询上级组织是否关联项目
        Map project = organizationMapper.getParentProject(record.getPid());
        if (project != null && project.size() > 0) {
            data.setProjectId(project.get("ProjectId") + "");
        }

        baseMapper.insert(data);
        return record;
    }

    @Override
    public Integer updateById(OrganizationForm record) {
        Organization data = this.convertDO(record);
        data.setOrgCompanyId(record.getCompanyName());
        data.setEditTime(new Date());
        //查询出所有的子组织
        //
        String id = data.getId();
        Map<Object, Object> map = new HashMap<>();
        List<OrganizationForm> list = new ArrayList<>();
        Map<Object, Object> hashMap = new HashMap<>();

        map.put("id", id);
        //获取父级的原组织名称
        Map parentOrg = organizationMapper.getParentProject(id);
        String oldOrgName = parentOrg.get("OrgName") + "";
        map.put("oldName", oldOrgName);
        map.put("newName", data.getOrgName());
        findAllChildOrg(map, list);
        if(list!=null&&list.size()>0){
            for (OrganizationForm org : list) {
                hashMap.put("id", org.getId());
                hashMap.put("fullPath", org.getFullPath().replaceAll(oldOrgName, data.getOrgName()));
                organizationMapper.updateChildFullPath(hashMap);
            }
        }
        data.setFullPath(data.getFullPath().replaceAll(oldOrgName, data.getOrgName()));
        //修改父级信息
        return baseMapper.updateById(data);
    }

    public void findAllChildOrg(Map map, List bigList) {
        Map<Object, Object> hashMap = new HashMap<>();
        //查询当前组织下的所有子集
        List<OrganizationForm> childOrgsList = organizationMapper.queryChildOrgs(map);
        if (childOrgsList != null & childOrgsList.size() > 0) {
            for (OrganizationForm org : childOrgsList) {
                bigList.add(org);
                hashMap.put("id", org.getId());
                findAllChildOrg(hashMap, bigList);
            }
        }

    }

    @Override
    public Integer deleteById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        return baseMapper.deleteById(id);
    }

    @Override
    public OrganizationVO selectById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new CessException(BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getCode(), BaseResultCodeEnum.ExeStats.PARAM_EMPTY.getMsg());
        }
        Organization data = baseMapper.selectById(id);
        OrganizationVO result = PropertyUtil.copy(data, OrganizationVO.class);
        return result;
    }

    @Autowired
    private OrganizationDao organizationMapper;

    @Override
    public Map selectAll(OrganizationForm record) {
        Map map = new HashMap();
        map.put("UserName", SecurityUtils.getUsername());
        Map userInfoMap = authMapper.mGetUserInfo(map);
        Map paramMap = new HashMap<>();
        paramMap.put("userName", SecurityUtils.getUsername());
        paramMap.put("jobCode", userInfoMap.get("JobCode") + "");
        paramMap.put("isNeedShow", record.getIsNeedShow());
        List<Organization> list = organizationMapper.findOrgListByOrgIdAndProIdAndCompanyId(paramMap);
        String jobCode = userInfoMap.get("JobCode") + "";
        if (jobCode.equals("20001")) {
            list.get(0).setPid("-1");
        }
        return this.convert(list);
    }

    @Override
    public IPage<OrganizationVO> selectPage(OrganizationForm record) {
        // form -> do 转换
        Organization data = PropertyUtil.copy(record, Organization.class);

        // 分页数据设置
        Page<Organization> page = new Page<>(record.getCurrent(), record.getSize());
        // 查询条件
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        queryWrapper.setEntity(data);
        IPage<Organization> list = baseMapper.findOrgListByOrgIdAndProIdAndCompanyId(page, record.getAuthCompanyId(),
                record.getProductId(), record.getId(), record.getPid());
        IPage<OrganizationVO> iPage = new Page<>();
        iPage.setRecords(PropertyUtil.copy(list.getRecords(), List.class));
        iPage.setCurrent(list.getCurrent());
        iPage.setSize(list.getSize());
        iPage.setTotal(list.getTotal());
        iPage.setPages(list.getPages());
        return iPage;
    }

    @Override
    public Map queryChildOrgs(Map paramMap) {
        // form -> do 转换
        Map resultMap = new HashMap<>();
        int pageIndex = Integer.parseInt(paramMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(paramMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        paramMap.put("pageIndex", i);
        List list = organizationMapper.queryChildOrgs(paramMap);
        String total = organizationMapper.queryChildOrgsCount(paramMap);
        resultMap.put("list", list);
        resultMap.put("total",Integer.parseInt(total));
        return resultMap;
    }

    @Override
    public List<Map> getAreaProjectRel(Map map) {
        List<Map> parlist=organizationMapper.getAreaProjectRel(map);//父
        List<Map> citylist=organizationMapper.getCityRel(map);//城市公司
            List<Map> itemlist=organizationMapper.getAreaProjectItemRel(map);//子

            for(Map data:citylist){
            List<Map> addItem=new ArrayList<>();
            for(Map data1:itemlist){
                List cityItem=new ArrayList();
               if(data.get("id").equals(data1.get("pid"))){
                   data1.remove("pid");
                   data1.remove("pname");
                   addItem.add(data1);
               }
            }
            data.put("children",addItem);
        }
        for (Map data : parlist) {
            List<Map> addItem=new ArrayList<>();
            for(Map data1:citylist){
                List cityItem=new ArrayList();
                if(data.get("id").equals(data1.get("pid"))){
                    data1.remove("pid");
                    data1.remove("pname");
                    addItem.add(data1);
                }
            }
            data.put("children",addItem);
        }
        return parlist;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addAreaProjectRel(List<Map> parmas) {
        //岗位项目授权
        //删除岗位项目关系
        organizationMapper.delAreaProjectRel(parmas.get(0).get("jobId")+"");
        //插入岗位项目关系
        organizationMapper.addAreaProjectRel(parmas);
        //岗位获取人员
        List<Map> userIds=organizationMapper.selectUserId(parmas.get(0).get("jobId")+"");
        if(null==userIds || userIds.isEmpty() || userIds.size()==0){

        }else {
              for(Map data: userIds){
                  //删除用户下面关系
                  organizationMapper.deleteUserProjectRel(data);
              }

            //初始化人员项目关系
            organizationMapper.addUserProjectRel(parmas.get(0).get("jobId") + "");
        }
    }

    @Override
    public Map getJiTuanProjectItemRel(Map map) {
        return organizationMapper.getJiTuanProjectItemRel(map);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int synOrgFourLevel() {
        organizationMapper.synOrgFourLevel();
        organizationMapper.updateProjectOrgId();

        organizationMapper.updateOrgLevel();
        //更新集团层级
        organizationMapper.updateJituanLevel();
        //更新区域事业部层级
        //首先查出来所有区域公司及事业部
        List<Map> list = organizationMapper.getQuYuCommony();
        for (Map map : list) {
            //更新区域事业部层级
          organizationMapper.updateQuYuLevel(map);
        }
        //更新城市级别
        List<Map> cityList = organizationMapper.getCityCompany();
        for (Map map : cityList) {
          organizationMapper.updateCityLevel(map);
        }
        //更新项目级别
        List<Map> projectList = organizationMapper.getProject();
        for (Map map : projectList) {
          organizationMapper.updateProjectLevel(map);
         organizationMapper.updateStageLevel(map);
        }

        //二次清洗
        List<Map> listTwo = organizationMapper.getQuYuCommony();
        for (Map map : listTwo) {
            //更新区域事业部层级
            organizationMapper.updateQuYuLevelTwo(map);
        }
        //更新城市级别
        List<Map> cityListTwo = organizationMapper.getCityCompany();
        for (Map map : cityListTwo) {
            organizationMapper.updateCityLevel(map);
        }
        //更新项目级别
        List<Map> projectListTwo = organizationMapper.getProject();
        for (Map map : projectListTwo) {
            organizationMapper.updateProjectLevel(map);
            organizationMapper.updateStageLevel(map);
        }

        //查询出层级关系
        List<Map> fourLevelMap = organizationMapper.getFourLevelRel();
        for (Map map : fourLevelMap) {
            Map parendMap = organizationMapper.getParentOrg(map.get("orgId")+"");
            while (parendMap!=null){
                if("4".equals(parendMap.get("orgLevel"))&&parendMap.get("parentLevelID").equals(parendMap.get("ID"))){
                    map.put("projectOrgId",parendMap.get("parentLevelID"));
                    map.put("projectOrgName",parendMap.get("parentLevelName"));
                }
                if("3".equals(parendMap.get("orgLevel"))&&parendMap.get("parentLevelID").equals(parendMap.get("ID"))){
                    map.put("cityOrgId",parendMap.get("parentLevelID"));
                    map.put("cityOrgName",parendMap.get("parentLevelName"));
                }
                if("2".equals(parendMap.get("orgLevel"))&&parendMap.get("parentLevelID").equals(parendMap.get("ID"))){
                    map.put("areaOrgId",parendMap.get("parentLevelID"));
                    map.put("areaOrgName",parendMap.get("parentLevelName"));
                }
                if("1".equals(parendMap.get("orgLevel"))&&parendMap.get("parentLevelID").equals(parendMap.get("ID"))){
                    map.put("groupOrgId",parendMap.get("parentLevelID"));
                    map.put("groupOrgName",parendMap.get("parentLevelName"));
                }
                parendMap = organizationMapper.getParentOrg(parendMap.get("PID")+"");
            }
        }


        //添加权限临时表
        organizationMapper.delJobOrgRel();
        organizationMapper.insertJobOrgRel(fourLevelMap);

     /*   //寻找组织的父级都有什么
        List<Map> jobOrgList = organizationMapper.getJobOrgRel();
        for (Map map : jobOrgList) {
            Map parendMap = organizationMapper.getParentOrg(map.get("orgId")+"");
            while (parendMap!=null){
                parendMap = organizationMapper.getParentOrg(map.get("PID")+"");
                if(parendMap!=null){
                   // if(parendMap.get("orgLevel"))
                }
            }
        }*/
        return 0;
    }

    @Override
    public ResultBody checkUserJobOrg(String userId, String menuId, String projectId) {
        //首先查询是否有多个岗位 如果有  则下一步  没有直接返回
        List<Map> jobList = organizationMapper.getUserJobs(userId);
        if(jobList.size()>1){
              //先去查询项目岗
            Map projectJobMap = organizationMapper.getProjectJob(userId,menuId,projectId);
            if(projectJobMap==null){
                projectJobMap =organizationMapper.getNextProjectJob(userId,menuId);
            }
            return ResultBody.success(projectJobMap);
        }else {
            return ResultBody.error(-10_0023,"未查询到兼岗！");
        }
    }


    /**
     * Form -> Do
     *
     * @param form 对象
     * @return Do对象
     */
    private Organization convertDO(OrganizationForm form) {
        Organization data = new Organization();
        data.setId(form.getId());
        data.setPid(form.getPid());
        data.setOrgCode(form.getOrgCode());
        data.setOrgName(form.getOrgName());
        data.setOrgShortName(form.getOrgShortName());
        data.setOrgCategory(form.getOrgCategory());
        data.setListIndex(form.getListIndex());
        data.setLevels(form.getLevels());
        data.setFullPath(form.getFullPath());
        data.setAuthCompanyID(form.getAuthCompanyId());
        data.setProductId(form.getProductId());
        data.setCreator(form.getCreator());
//        data.setCreateTime(DateUtil.parseTime(form.getCreateTime()));
        data.setEditor(form.getEditor());
//        data.setEditTime(DateUtil.parseTime(form.getEditTime()));
        data.setStatus(form.getStatus());
        data.setIsDel(form.getIsDel());
        data.setCurrentPoint(form.getCurrentPoint());
        data.setProjectId(form.getProjectId());
        data.setOrgCompanyId(form.getOrgCompanyId());
        data.setOrgType(form.getOrgType());
        return data;
    }

    /**
     * Do -> VO
     *
     * @param list 对象
     * @return VO对象
     */
    private Map convert(List<Organization> list) {
        List<OrganizationVO> organizationList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return MapUtil.newHashMap();
        }
        OrganizationVO target = null;
        for (Organization source : list) {
            target = new OrganizationVO();
            BeanUtils.copyProperties(source, target);
            organizationList.add(target);
        }
        Map map = this.buildTree(organizationList);
        return map;
    }


    @Override
    public Integer updateStatusById(OrganizationForm organizationForm) {
        try {
            String username = SecurityUtils.getUsername();
            organizationForm.setUserName(username);
            return this.baseMapper.updateStatusById(organizationForm);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(-20_0001, e);
        }
    }

    /**
     * 构建 orgTree
     *
     * @param orgList
     * @return
     */
    public Map buildTree(List<OrganizationVO> orgList) {
        List<OrganizationVO> trees = CollUtil.newArrayList();
        for (OrganizationVO vo : orgList) {

            if ("-1".equals(vo.getPid())) {
                trees.add(vo);
            }
            for (OrganizationVO organizationVO : orgList) {
                if (organizationVO.getPid() != null && vo.getId() != null) {
                    if (organizationVO.getPid().equals(vo.getId())) {
                        if (vo.getChildren() == null) {
                            vo.setChildren(new ArrayList<OrganizationVO>());
                        }
                        vo.getChildren().add(organizationVO);
                    }
                }
            }
        }
        Map map = MapUtil.newHashMap();
        map.put("content", trees.size() == 0 ? orgList : trees);
        map.put("totalElements", orgList != null ? orgList.size() : 0);
        return map;
    }
}
