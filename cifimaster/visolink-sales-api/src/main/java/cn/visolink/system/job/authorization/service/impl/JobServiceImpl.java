package cn.visolink.system.job.authorization.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.visolink.common.redis.service.RedisService;
import cn.visolink.constant.VisolinkConstant;
import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.system.job.authorization.mapper.JobMapper;
import cn.visolink.system.job.authorization.service.JobService;
import cn.visolink.system.org.model.vo.OrganizationVO;
import cn.visolink.system.org.service.impl.OrganizationServiceImpl;
import cn.visolink.utils.CommUtils;
import cn.visolink.utils.EncryptUtils;
import cn.visolink.utils.TreeUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2019.09.10
 */

@Service
public class JobServiceImpl implements JobService {

    @Resource(name = "jdbcTemplate")
    private JdbcTemplate jt;

    @Autowired
    private JobMapper jobMapper;

    @Autowired
    private OrganizationServiceImpl organizationService;

    /**
     * 获取所有岗位
     */
    @Override
    public PageInfo<Map> getJobByAuthId(Map map) {
        PageHelper.startPage(Integer.parseInt(map.get("pageNum").toString()), Integer.parseInt(map.get("pageSize").toString()));
        List<Map> list = jobMapper.getJobByAuthId(map);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 查询所有组织架构
     */
    @Override
    public List<Map> getAllOrg(Map map) {
        return jobMapper.getAllOrg(map);
    }

    @Override
    public ResultBody getOrgData(Map map){
        try {
            List<OrganizationVO> jobMapperOrgData = jobMapper.getOrgData(map);
            Map buildTree = organizationService.buildTree(jobMapperOrgData);
            return  ResultBody.success(buildTree);
        }catch (Exception e){
            e.printStackTrace();
            return  ResultBody.error(-1945,"组织列表查询失败!");
        }

    }
    @Override
    public List<Map> getAllCommonJob(Map map) {
        return jobMapper.getAllCommonJob(map);
    }

    /**
     * 查询岗位下的人员列表，或根据姓名查询人员
     *
     * @param reqMap
     * @return
     */
    @Override
    public Map getSystemUserList(Map reqMap) {
        // form -> do 转换
        Map resultMap = new HashMap<>();
        int pageIndex = Integer.parseInt(reqMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        reqMap.put("pageIndex", i);
        List<Map> userList = jobMapper.getSystemJobUserList(reqMap);
        Integer count = jobMapper.getSystemJobUserListCount(reqMap);
        resultMap.put("list", userList);
        resultMap.put("total", count);
        return resultMap;
    }

    /**
     * 获取当前和下属所有组织岗位
     *
     * @param reqMap
     * @return
     */
    @Override
    public List<Map> getSystemJobAllList(Map reqMap) {
        return jobMapper.getSystemJobAllList(reqMap);
    }

    /**
     * 新增岗位-插入Jobs信息
     *
     * @param reqMap
     * @return
     */
    @Override
    public int saveSystemJobForManagement(Map reqMap) {

        //先获取数据库最大的自定义code
        String jobCode = jobMapper.getJobCodeMax();
        String newCode=(Integer.parseInt(jobCode.substring(3,jobCode.length()))+1)+"";
        reqMap.put("JobCode","ZDY"+newCode);
        reqMap.put("ID", UUID.randomUUID().toString());
        int i = jobMapper.saveSystemJobForManagement(reqMap);
        int i1 = jobMapper.saveSystemJobForManagement2(reqMap);
        //刷新岗位组织
        Map map= jobMapper.getFourOrgLevelByJobId(reqMap.get("ID")+"");
        if(map!=null&&map.get("orgId")!=null){
            Map parendMap = jobMapper.getParentOrg(map.get("orgId") + "");
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
                parendMap = jobMapper.getParentOrg(parendMap.get("PID")+"");
            }
        }
        jobMapper.insertJobOrgRel(map);
        return i + i1;
    }

    /**
     * 组织岗位功能列表查询(前端功能授权)
     *
     * @param reqMap
     * @return
     */
    @Override
    public Map getSystemJobAuthByUserId(Map reqMap) {

        //登录人有权限的菜单
        List<Map> userMenus = jobMapper.userMenusByUserId(reqMap);

        //登录人有权限的功能
        List<Map> userFunctions = jobMapper.userFunctionsByUserId(reqMap);

        //该岗位已有的菜单和功能
        ArrayList<Map> jobFunction1 = (ArrayList<Map>) jobMapper.jobFunctionsByUserId(reqMap);
        CopyOnWriteArrayList<Map> jobFunctions = new CopyOnWriteArrayList<>(jobFunction1);

        List<Map> oneList = new CopyOnWriteArrayList<>();
        List<Map> twoList = new CopyOnWriteArrayList<>();
        List<Map> threeList = new CopyOnWriteArrayList<>();
        List<Map> fourList = new CopyOnWriteArrayList<>();

        for (Map userMenu : userMenus) {
            String id = userMenu.get("ID") + "";
            String levels = userMenu.get("Levels").toString();
            switch (levels) {
                case "1":
                    oneList.add(userMenu);
                    break;
                case "2":
                    twoList.add(userMenu);
                    break;
                case "3":
                    threeList.add(userMenu);
                    break;
                case "4":
                    fourList.add(userMenu);
                    break;
            }
            for (Map jobFunction : jobFunctions) {
                String jobFunId = jobFunction.get("ID") + "";
                if (id.equals(jobFunId)) {
                    userMenu.put("flag", true);
                    jobFunctions.remove(jobFunction);
                }
            }
        }

        List<Map> child = new ArrayList<>(8);
        for (Map threeMap : threeList) {
            String id = threeMap.get("ID").toString();
            for (Map fourMap : fourList) {
                String pid = fourMap.get("PID").toString();
                if (id.equals(pid)) {
                    child.add(fourMap);
                    fourList.remove(fourMap);
                }
            }
            threeMap.put("child", child);
            child.clear();
        }

        for (Map twoMap : twoList) {
            String id = twoMap.get("ID").toString();
            for (Map threeMap : threeList) {
                String pid = threeMap.get("PID").toString();
                if (id.equals(pid)) {
                    child.add(threeMap);
                    threeList.remove(threeMap);
                }
            }
            twoMap.put("child", child);
            child.clear();
        }

        for (Map oneMap : oneList) {
            String id = oneMap.get("ID").toString();
            for (Map twoMap : twoList) {
                String pid = twoMap.get("PID").toString();
                if (id.equals(pid)) {
                    child.add(twoMap);
                    twoList.remove(twoMap);
                }
            }
            oneMap.put("child", child);
            child.clear();
        }


        HashMap<String, Object> map = MapUtil.newHashMap();
        map.put("userMenus", oneList);
//        map.put("userFunctions", userFunctions);
//        map.put("jobFunctions", jobFunctions);
        return map;
    }

    /**
     * 前后端功能授权保存
     *
     * @param paramMap
     * @return
     */
    @Override
    public String saveSystemJobAuthByManagement(Map paramMap) {
        String OldMenus = String.valueOf(paramMap.get("OldMenus"));

        OldMenus = OldMenus.replaceAll("\\|", "','");
        Map<String, String> pa = new HashMap<>();
        pa.put("JobID", String.valueOf(paramMap.get("JobID")));
        pa.put("OldeMenuID", OldMenus);
        pa.put("MenusType", String.valueOf(paramMap.get("MenusType")));

        List<Map> resList = jobMapper.getSystemJobMenusID(paramMap);

        if (resList != null) {
            for (Map map : resList) {
                jobMapper.removeSystemJobAuth(map);
            }
        }

        // //删除原来的菜单
        String newMenus = String.valueOf(paramMap.get("Menus"));
        if (StringUtil.isEmpty(newMenus)) {
            return "保存失败！";
        }

        String[] menusArray = newMenus.split("\\|");

        int i = 0;
        if (menusArray.length > 0) {
            for (i = 0; i < menusArray.length; i++) {
                Map<String, String> dataMap = new HashMap<>();
                dataMap.put("JobID", String.valueOf(paramMap.get("JobID")));
                dataMap.put("MenuID", menusArray[i]);
                jobMapper.saveSystemJobAuthManagement(dataMap);
            }
        }
        if (i > 0) {
            return "保存成功";
        }
        return "保存失败";
    }

    /**
     * 修改岗位信息
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemJobByUserId(Map reqMap) {
        int i = jobMapper.modifySystemJobByUserId(reqMap);
        //刷新岗位组织
        Map map= jobMapper.getFourOrgLevelByJobId(reqMap.get("ID")+"");
        if(map!=null&&map.get("orgId")!=null){
            Map parendMap = jobMapper.getParentOrg(map.get("orgId") + "");
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
                parendMap = jobMapper.getParentOrg(parendMap.get("PID")+"");
            }
            jobMapper.insertJobOrgRel(map);
        }


//        int i1 = jobMapper.saveSystemJobForManagement2(reqMap);
        return i;
    }

    /**
     * 删除岗位信息
     *
     * @param reqMap
     * @return
     */
    @Override
    public int removeSystemJobByUserId(Map reqMap) {

        return jobMapper.removeSystemJobByUserId(reqMap);
    }

    /**
     * 查询引入用户
     *
     * @param reqMap
     * @return
     */
    @Override
    public Map pullinUser(Map reqMap) {
        Map resultMap = new HashMap<>();
        int pageIndex = Integer.parseInt(reqMap.get("pageIndex").toString());
        int pageSize = Integer.parseInt(reqMap.get("pageSize").toString());
        int i = (pageIndex - 1) * pageSize;
        reqMap.put("pageIndex", i);
        List<Map> list = jobMapper.getIntroducingUsers(reqMap);
        Integer count = jobMapper.getIntroducingUsersCount(reqMap);
        resultMap.put("list", list);
        resultMap.put("total", count);
        return resultMap;
    }

    /**
     * 保存用户
     *
     * @param map
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveSystemUser(Map map) {
        //岗位ID
        String jobId = map.get("jobId").toString();
        String authCompanyId = map.get("authCompanyId").toString();
        String orgId = map.get("orgId").toString();
        List<Map> data = (List) map.get("data");
        String productId1 = map.get("productId").toString();
        String userId = map.get("userId").toString();

        //判断插入的用户是否存在
        CopyOnWriteArrayList<Map> maps = new CopyOnWriteArrayList<>(data);
        for (Map map1 : maps) {
            Map userISHaving = jobMapper.getSystemUserNameExists(map1);

            //用户存在
            if (userISHaving!=null) {
               map1.put("accountId",userISHaving.get("ID"));
            }

        }
        List<Map> maps1 = new ArrayList<>(maps);

            for (Map datum : maps1) {
            String ID = UUID.randomUUID().toString();
            datum.put("authCompanyId", authCompanyId);
            datum.put("orgId", orgId);
            datum.put("productId", productId1);
            datum.put("creator", userId);
            datum.put("jobId", jobId);
            datum.put("ID", ID);
            if(datum.get("accountId")!=null){
                Map jobMap=jobMapper.isRepeat(datum.get("accountId").toString(),jobId);
                Map currenJob=jobMapper.isCurrentJob(datum.get("accountId").toString());
                //保存平台与岗位的关系
                datum.put("ID",datum.get("accountId"));
                if(currenJob==null){
                    datum.put("CurrentJob",1);
                }else{
                    datum.put("CurrentJob",0);
                }
                if(jobMap==null){
                   jobMapper.saveJobSuserrel(datum);
                }else{
                    //表示岗位已存在
                    return 1001;
                }
            }else{
                //保存账号表
                datum.put("CurrentJob",1);
              jobMapper.saveIntroducingUsers(datum);
              jobMapper.saveJobSuserrel(datum);
                return 0;
            }
        }

      return 0;

    }

    /**
     * 删除用户信息
     *
     * @param reqMap
     * @return
     */
    @Override
    public int removeSystemJobUserRel(Map reqMap) {

        int i = jobMapper.removeSystemJobUserRel(reqMap);
        return i;
    }

    /**
     * 修改用户
     *
     * @param reqMap
     * @return
     */
    @Override
    public int modifySystemJobUserRel(Map reqMap) {
        Map map = (Map) reqMap.get("data");
     if("男".equals(map.get("Gender"))){
         map.put("Gender",1);
     }
     if("女".equals(map.get("Gender"))){
         map.put("Gender",2);
     }
     if("启用".equals(map.get("Status"))){
         map.put("Status",1);
     }
     if("禁用".equals(map.get("Status"))){
         map.put("Status",0);
     }
     if("Saas账号".equals(map.get("AccountType"))){
         map.put("AccountType",1);
     }
     if("普通账号".equals(map.get("AccountType"))){
         map.put("AccountType",2);
     }
        System.out.println(map);
        int i = jobMapper.modifySystemJobUserRel(map);
        return i;
    }

    @Override
    public int saveSystemJobUserRel(Map map) {

        //岗位ID
        String jobId = map.get("jobId").toString();
        String authCompanyId = map.get("authCompanyId").toString();
        String orgId = map.get("orgId").toString();
        Map data = (Map) map.get("data");
        String productId = map.get("productId").toString();
        String userId = map.get("userId").toString();
        String password = data.get("Password") + "";
        if(data.get("Password")==null||"".equals(password)||"null".equalsIgnoreCase(password)){
            data.put("Password", EncryptUtils.encryptPassword("1"));
        }else{
            data.put("Password", EncryptUtils.encryptPassword(data.get("Password").toString()));
        }
        data.put("alias",data.get("UserName"));
        Map userISHaving = jobMapper.getSystemUserNameExists(data);
        String ID = UUID.randomUUID().toString();
        data.put("AuthCompanyID", authCompanyId);
        data.put("UserOrgID", orgId);
        data.put("ProductID", productId);
        data.put("Creator", userId);
        data.put("JobID", jobId);
        data.put("ID", ID);
        if (userISHaving!=null) {
            //用户存在
            data.put("ID", userISHaving.get("ID"));
            //判断是否存在相同岗位
            Map jobMap=jobMapper.isRepeat(userISHaving.get("ID")+"",jobId);
            Map currenJob=jobMapper.isCurrentJob(userISHaving.get("ID")+"");
            if(currenJob==null){
                data.put("CurrentJob",1);
            }else{
                data.put("CurrentJob",0);
            }
            if(jobMap==null){
                jobMapper.saveAccountToJobUserURl(data);
            }else{
                //表示岗位已存在
                return 1001;
            }
        }else{
            data.put("CurrentJob",1);
            //保存账号表
            jobMapper.saveSystemUser(data);
            //保存平台与岗位的关系
            jobMapper.saveAccountToJobUserURl(data);
        }

      return 0;

    }

    /**
     * 获取所有菜单
     * @return
     */
    @Override
    public ResultBody getAllMenu(String jobId) {
        List<Map> list=jobMapper.getAllMenu();

        Map menusMap = CommUtils.buildTree(list);
        List<Map> jobMenu=jobMapper.getJobMenu(jobId);
        menusMap.put("jobRelMenu",jobMenu);
        return ResultBody.success(menusMap);
    }

    /**
     * 获取通用岗位所有菜单
     * @return
     */
    @Override
    public ResultBody getCommonAllMenu(String jobId) {
        List<Map> list=jobMapper.getAllMenu();
        Map menusMap = CommUtils.buildTree(list);
        List<Map> jobMenu=jobMapper.getCommonMenu(jobId);
        menusMap.put("jobRelMenu",jobMenu);
        return ResultBody.success(menusMap);
    }
    /**
     * 获取通用岗位所有报表
     * @return
     */
    @Override
    public ResultBody getCommonAllReportMenu(String jobId) {
        List<Map> list=jobMapper.getAllReportMenu();
        Map menusMap = CommUtils.buildTree(list);
        List<Map> jobMenu=jobMapper.getCommonAllReportMenu(jobId);
      for (int i=0;i<jobMenu.size();i++){
          String levels=jobMenu.get(i).get("levels")+"";
          if("1".equals(levels)||"0".equals(levels)){
              jobMenu.remove(jobMenu.get(i));
              i--;
          }
      }

        menusMap.put("jobRelMenu",jobMenu);
        return ResultBody.success(menusMap);
    }


    /**
     * 获取所有菜单
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveJobMenus(Map map, String jobId) {
       try {
           jobMapper.delJobMRelMenu(jobId);
           //取消风控报表的授权

           List joblist= (List) map.get("jobList");
           List isLasts= (List) map.get("isLast");
           if(map.get("jobList")!=null&&joblist.size()>0&&isLasts!=null&&isLasts.size()>0){
               String str=map.get("jobList").toString().replace("[","").replace("]","");
               String isLast=map.get("isLast").toString().replace("[","").replace("]","");
               String[] strArray = str.split(", ");
               String[] isLastArray = isLast.split(", ");
               for (int i = 0; i < strArray.length; i++) {
                   System.out.println("jobId："+jobId);
                   System.out.println("menuId："+strArray[i]);
                   if(Integer.parseInt(isLastArray[i].toString())==0){
                       System.out.println(strArray[i]+"父节点不添加");
                   }else{
                       jobMapper.saveJobMenu(jobId,strArray[i]);
                   }
               }
           }
           return ResultBody.error(200,"保存成功！");
       }catch (Exception e){
           throw new BadRequestException(-14_0001,e);
       }
    }
    /**
     * 保存菜单
     * @retu通用rn
     */

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody saveCommonJobMenus(Map map, String jobId) {
        try {
            jobMapper.delCommonJobMRelMenu(jobId);
            List joblist= (List) map.get("jobList");
            List isLasts= (List) map.get("isLast");
            if(map.get("jobList")!=null&&joblist.size()>0&&isLasts!=null&&isLasts.size()>0){
               String str=map.get("jobList").toString().replace("[","").replace("]","");
               String isLast=map.get("isLast").toString().replace("[","").replace("]","");
               String[] strArray = str.split(", ");
               String[] isLastArray = isLast.split(", ");
               for (int i = 0; i < strArray.length; i++) {
                   System.out.println("jobId："+jobId);
                   System.out.println("menuId："+strArray[i]);
                   //父节点不添加授权
                   if(Integer.parseInt(isLastArray[i].toString())==0){
                   }else{
                       jobMapper.saveCommonJobMenu(jobId,strArray[i]);
                   }
               }
           }
            //清除redis用户缓存
            return ResultBody.error(200,"保存成功!");
        }catch (Exception e){
            throw new BadRequestException(-14_0001,e);
        }
    }

    @Override
    public ResultBody saveCommonReportMenu(Map map) {
        try {
            Map paramMap=new HashMap<>();
           String jobId=map.get("jobId")+"";
            jobMapper.delCommonReportMRelMenu(jobId);
            if(map.get("jobList")!=null){
                String str=map.get("jobList").toString().replace("[","").replace("]","");
                String[] strArray = str.split(", ");
                for (int i = 0; i < strArray.length; i++) {
                    paramMap.put("jobId",jobId);
                    paramMap.put("menuId",strArray[i]);
                    jobMapper.saveCommonReportMenu(paramMap);
                }
            }
            return ResultBody.error(200,"授权成功！");
        }catch (Exception e){
            throw new BadRequestException(-14_0001,e);
        }
    }

    /**
     * 中介公司
     * @retu通用rn
     */
    @Override
    public ResultBody getAllCompanyInfo() {
        return ResultBody.success(jobMapper.getAllCompanyInfo());
    }
    /**
     * 所属组织
     *
     */
    @Override
    public ResultBody getAllOrgProject() {
         List<Map> list=jobMapper.getAllOrgProject();
        Map menusMap = CommUtils.buildTree(list);
        return ResultBody.success(menusMap);
    }
 /**
     * 所属区域集团
     *
     */
    @Override
    public ResultBody getAllOrgProject2() {
         List<Map> list=jobMapper.getAllOrgProject2();
        Map menusMap = CommUtils.buildTree(list);
        return ResultBody.success(menusMap);
    }

    /**
     * 更新项目
     * @retu通用rn
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultBody updateProject(Map map) {
        if(map!=null){
            jobMapper.updateProjectId(map);
            String fullPath=jobMapper.getFullPath(map.get("orgId").toString());
            jobMapper.updateOrg(map.get("projectId").toString(),fullPath);
        }
        return ResultBody.error(200,"成功！");
    }
    @Override
    public ResultBody getAuthorizationData(Map map) {
        Map<Object, Object> paramMap = new HashMap<>();
        paramMap.put("jobId", map.get("jobId") + "");
        List<Map> list = jobMapper.getAllFunctions(paramMap);
        Map menusMap = CommUtils.buildTree(list);
        List<Map> listData = jobMapper.getAccreditFunctions(paramMap);
        for (int i=0;i<listData.size();i++){
            String levels=listData.get(i).get("levels")+"";
            if("1".equals(levels)||"0".equals(levels)){
                listData.remove(listData.get(i));
                i--;
            }
        }
        menusMap.put("jobRelMenu", listData);
        return ResultBody.success(menusMap);

    }

    @Override
    public ResultBody saveAuthorization(Map map) {
        if(map.get("reportIdList")!=null){
            String reportIdList = map.get("reportIdList") + "";
            String jobId = map.get("jobId") + "";
            String string = reportIdList.replace("[", "").replace("]", "").toString();
            string=string.replace(" ","");
            Map<Object, Object> paramMap = new HashMap<>();
            //删除已经授权的功能
            jobMapper.deletBeAuthorized(map);
            String[] splitReportIdList = string.split(",");
            if (splitReportIdList.length > 0) {
                for (String s : splitReportIdList) {
                    String pid = jobMapper.getAuthorizedsParent(s);
                    //如果授权功能为顶级功能，则不进行授权
                    if(!"-1".equals(pid)){
                        paramMap.put("jobId", jobId);
                        paramMap.put("ReportId", s);
                        jobMapper.saveAuthorizeds(paramMap);
                    }

                }
            }
        }
        ResultBody resultBody = new ResultBody();
        resultBody.setMessages("授权成功！");
        resultBody.setCode(200);
        return resultBody;
    }
}
