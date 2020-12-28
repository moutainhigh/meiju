package cn.visolink.system.projectmanager.service.impl;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.salesmanage.datainterface.service.Datainterfaceservice;
import cn.visolink.salesmanage.plandatainterface.service.PlanDataInterfaceservice;
import cn.visolink.system.projectmanager.dao.projectmanagerDao;
import cn.visolink.system.projectmanager.service.projectmanagerService;
import com.alibaba.fastjson.JSON;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Service
public class projectmanagerServiceImpl implements projectmanagerService {
    @Autowired
    projectmanagerDao managerDao;

    @Autowired
    Datainterfaceservice datainterfaceservice;

    @Autowired
    PlanDataInterfaceservice planDataInterfaceservice;
    /**
     * 项目管理查询
     */
    @Override
    public Map projectListSelect(Map<String, Object> map) {
        /**
         * 前台需要翻页，前台增加pagenum和pagesize两个参数
         * ，在param里封装pagenum和pagesize两个参数来用于在后台进行翻页
         * */
        try {
            if (!map.isEmpty()) {
                if (map.get("pageIndex") == null) {
                    map.put("pageIndex", 1);
                }
                if (map.get("pageSize") == null) {
                    map.put("pageSize", 10);
                }
                int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
                int pageSize = Integer.parseInt(map.get("pageSize").toString());
                int i = (pageIndex - 1) * pageSize;
                map.put("pageIndex", i);
                List<Map<String, Object>> list = managerDao.projectListSelect(map);
                Integer total = managerDao.projectListSelectCount(map);
                Map<Object, Object> resultMap = new HashMap<>();
                resultMap.put("list", list);
                resultMap.put("total", total);
                return resultMap;
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0001, e);
        }
        return null;
    }

    @Override
    public List<Map> exportProject(Map map) {
        List<Map> list = managerDao.exportProject(map);
        return list;
    }

    /**
     * 启用禁用项目
     */
    @Override
    public Integer projectIsEnableUpdate(Map<String, Object> map) {
        try {
            if (!map.isEmpty()) {
                return managerDao.projectIsEnableUpdate(map);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0002, e);
        }
        return null;

    }

    /**
     * 删除项目
     */
    @Override
    public Integer projectDeleteUpdate(Map<String, Object> map) {
        try {
            if (!map.isEmpty()) {
                return managerDao.projectDeleteUpdate(map);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0003, e);
        }
        return null;
    }

    /*
     * 判断项目编号是否已存在
     * */
    @Override
    public Map<String, Object> projectNumIsExsit(Map<String, String> dataMap) {
        try {
            if (!dataMap.isEmpty()) {
                return managerDao.projectNumIsExsit(dataMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0011, e);
        }
        return null;
    }


    /*新增销售系统与售前系统的关联,先跟新，然後插入，这里是跟新
     * 与售前项目相关联,和AddNewProjectSaleRelInsert一起被调用
     * */
    @Override
    public Integer addNewProjectSaleRelUpdate(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.addNewProjectSaleRelUpdate(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0015, e);
        }
        return null;
    }

    /*
     * 新增销售系统与售前系统的关联,先跟新，然後插入，这里是插入数据
     * 与售前项目相关联,和addNewProjectSaleRelUpdate一起被调用
     * */
    @Override
    public Integer addNewProjectSaleRelInsert(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.addNewProjectSaleRelInsert(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0016, e);
        }
        return null;

    }


    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是查找项目ID
     * */
    @Override
    public Map<String, String> newProjectOrgSelect(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.newProjectOrgSelect(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0013, e);
        }
        return null;

    }

    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是跟新项目
     * */
    @Override
    public Integer newProjectOrgUpdate(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.newProjectOrgUpdate(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0014, e);
        }
        return null;
    }

    /*
     * 修改项目信息
     * */
    @Override
    public Integer projectInfoModify(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.projectInfoModify(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0005, e);
        }
        return null;
    }

    /*
     * 判断当前是否已经存在过关联关系
     * */
    @Override
    public Map<String, Object> projectSaleRelCountBySaleProjectIdSelect(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.projectSaleRelCountBySaleProjectIdSelect(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0012, e);
        }
        return null;
    }

    /*
     * 若产生关联关系（>0）且则走这一条方法
     * */
    @Override
    public Integer newProjectSaleRelNoDel(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.newProjectSaleRelNoDel(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0012, e);
        }
        return null;
    }

    /*
     * 增加一条新的项目
     * */
    @Override
    public Integer addNewProjectInfoInsert(Map<String, String> projectMap) {
        try {
            if (!projectMap.isEmpty()) {
                return managerDao.addNewProjectInfoInsert(projectMap);
            }
        } catch (Exception e) {
            throw new BadRequestException(-13_0011, e);
        }
        return null;
    }

    /*
     * 增加项目和修改项目的调用方法
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String projectexecute(Map<String, String> dataMap, HttpServletRequest request, HttpServletResponse response) {
        String r = "";
        Map<String, Object> returnMap = new LinkedHashMap<>();
        String res = "";
        try {
            String reqType = dataMap.get("reqType");
            String oldProjectNum = dataMap.get("OldProjectNum");
            String ProjectNum = dataMap.get("ProjectNum");
            if (!oldProjectNum.equals(ProjectNum)) {

                /*
                 * 判断项目编号是否已存在
                 * */

                Map<String, Object> projectInfo = projectNumIsExsit(dataMap);
                if (projectInfo != null) {
                    returnMap.put("errcode", "-1");
                    returnMap.put("errmsg", "项目编号已存在");
                    return JSON.toJSONString(returnMap);

                }

            }
            /*
             * 查看被调用的是新增还是修改
             * */
            switch (reqType) {
                case "addNewProject":

                    returnMap = addNewProjectInfo(dataMap);
                    break;


                case "modifyProjectInfo":
                    returnMap = updateProjectInfo(dataMap);
                    break;
            }
        } catch (Exception e) {

            returnMap.put("errcode", "-1");
            returnMap.put("errmsg", "系统错误，请联系管理员!");
            e.printStackTrace();
            throw new BadRequestException(-13_0010, e);

        }

        return JSON.toJSONString(returnMap);
    }


    /*
     * 增加一条新的项目(联合所有需要增加项目的方法，在控制台被调用)
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addNewProjectInfo(Map<String, String> projectMap) {

        Map<String, Object> returnMap = new HashMap<>();

        try {

            String ProjectID = UUID.randomUUID().toString();
            projectMap.put("ID", ProjectID);

            /*
             * 以下方法为插入一条项目信息时需要调用的接口
             * */
            addNewProjectInfoInsert(projectMap);
            managerDao.addNewProjectSaleRelUpdateProjectorgrel(projectMap);
            managerDao.addNewProjectSaleRelUpdateOne(projectMap);
            managerDao.addNewProjectSaleRelUpdateTwo(projectMap);
            managerDao.addNewProjectSaleRelUpdateThree(projectMap);
            managerDao.addNewProjectSaleRelUpdateFour(projectMap);
            managerDao.addNewProjectSaleRelUpdateFive(projectMap);
            managerDao.addNewProjectSaleRelUpdateSix(projectMap);
            managerDao.addNewProjectSaleRelUpdateSeven(projectMap);
            managerDao.addNewProjectSaleRelUpdateEight(projectMap);


            String SaleProjectID = projectMap.get("SaleProjectID");
            if (!StringUtil.isNullOrEmpty(SaleProjectID)) {
                    /*
                       是否关联销售系统项目
                    *
                    * */
                addNewProjectSaleRelUpdate(projectMap);
                addNewProjectSaleRelInsert(projectMap);
            }

            /*
             * 添加完成修改对应组织的项目ID,查询当前组织的项目ID
             * */
            updateOrgProject(projectMap);
            returnMap.put("errcode", 0);
            returnMap.put("errmsg", "项目新增成功!");

        } catch (Exception e) {
            returnMap.put("errcode", "-1");
            returnMap.put("errmsg", "系统内部出错，请联系管理员");

            throw new BadRequestException(-13_0004, e);

        }

        return returnMap;
    }

    /*
     * 添加完成修改对应组织的项目ID,查询当前组织的项目ID
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrgProject(Map<String, String> projectMap) {
        try {
            Map<String, String> orgMap = newProjectOrgSelect(projectMap);
            orgMap.put("NewProjectID", projectMap.get("ID") + "");
            newProjectOrgUpdate(orgMap);

        } catch (Exception e) {
            throw new BadRequestException(-13_0006, e);

        }
    }

    /*
     * 修改项目信息,该方法要被controller层调用，集合需要修改项目信息方法的接口和逻辑
     * */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateProjectInfo(@RequestBody Map<String, String> projectMap) {

        Map<String, Object> returnMap = new HashMap<>();

        try {

            /*
             * 修改项目信息
             * */
            projectInfoModify(projectMap);
            projectMap.put("SaleIsDel", "1");
            /*
             * 把原来的关联项目删除，不管有没有新增关联
             * */
            addNewProjectSaleRelUpdate(projectMap);
            String SaleProjectID = projectMap.get("SaleProjectID");
            if (!StringUtil.isNullOrEmpty(SaleProjectID)) {
                /*
                 * 是否关联销售系统项目，判断当前内容不为空时，判断当前是否已经存在过关联关系
                 * */
                Map<String, Object> isSaleMap = projectSaleRelCountBySaleProjectIdSelect(projectMap);
                if (isSaleMap.size() > 0) {
                    long count = (long) isSaleMap.get("A");
                    if (count > 0) {
                        /*
                         * 当前已存在关联关系，执行此方法
                         * */
                        newProjectSaleRelNoDel(projectMap);
                    } else {
                        addNewProjectSaleRelInsert(projectMap);
                    }
                } else {
                    addNewProjectSaleRelInsert(projectMap);
                }


            }
            returnMap.put("errcode", 0);
            returnMap.put("errmsg", "项目修改成功!");

        } catch (Exception e) {

            e.printStackTrace();

            returnMap.put("errcode", "-1");
            returnMap.put("errmsg", "系统内部出错，请联系管理员");
            throw new BadRequestException(-13_0009, e);

        }

        return returnMap;
    }

    @Override
    public List<Map<String, Object>> selectOneProject(Map<String, Object> map) {
        return managerDao.selectOneProject(map);
    }

    @Override
    public Integer updateMenuStatus(Map<String, Object> map) {
        return managerDao.updateMenuStatus(map);
    }

    @Override
    public Map systemmenus(Map map) {

        return null;
    }

    @Override
    public List getWglProject(Map map) {
        List<Map> wglProject = managerDao.getWglProject(map);
        return wglProject;
    }

    @Override
    public int delPRoject(){
        return managerDao.delProject();
    }

    @Override
    public ResultBody addGlProject(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {

            List<Map> projectList=(List<Map>)map.get("projectList");
            if(projectList!=null){
                    for (Map<String, Object> project : projectList) {
                        managerDao.addGlProject(project);
                }
                    //引入明源操盘项目
                planDataInterfaceservice.insertBasic();
                //引入周计划项目执行生成产品树
                datainterfaceservice.initmmidm();
                datainterfaceservice.updataBusinsee();

            }
            resultBody.setMessages("项目引入成功!");
            resultBody.setCode(200);
            return  resultBody;
        }catch (Exception e){
            e.printStackTrace();
            resultBody.setMessages("项目引入失败!");
            resultBody.setCode(-0450);
            return  resultBody;
        }

    }

    @Override
    public ResultBody updateProject(Map map) {
        ResultBody<Object> resultBody = new ResultBody<>();
        try {
            if(map.get("project_stage")!=null){
                if(map.get("project_stage").equals("首开阶段")){
                    map.put("project_stage",0);
                }else if(map.get("project_stage").equals("续销阶段")){
                    map.put("project_stage",1);
                }else if(map.get("project_stage").equals("尾盘阶段")){
                    map.put("project_stage",2);
                }
            }
        /*    if(map.get("trader_type")!=null){
                if(map.get("trader_type").equals("独立操盘")){
                    map.put("trader_type",0);
                }else if(map.get("trader_type").equals("联合操盘")){
                    map.put("trader_type",1);
                }else if(map.get("trader_type").equals("非操盘")){
                    map.put("trader_type",2);
                }
            }*/

            if(map.get("sales_master_type")!=null){
                if(map.get("sales_master_type").equals("是")){
                    map.put("sales_master_type",1);
                }else if(map.get("sales_master_type").equals("否")){
                    map.put("sales_master_type",0);
                }
            }

            if(map.get("cifi_assume_money")!=null){
                if(map.get("cifi_assume_money").equals("是")){
                    map.put("cifi_assume_money",1);
                }else if(map.get("cifi_assume_money").equals("否")){
                    map.put("cifi_assume_money",0);
                }
            }
            if(map.get("tenement_wp_project")!=null){
                if(map.get("tenement_wp_project").equals("是")){
                    map.put("tenement_wp_project",1);
                }else if(map.get("tenement_wp_project").equals("否")){
                    map.put("tenement_wp_project",0);
                }
            }
            if(map.get("business_travel_project")!=null){
                if(map.get("business_travel_project").equals("是")){
                    map.put("business_travel_project",1);
                }else if(map.get("business_travel_project").equals("否")){
                    map.put("business_travel_project",0);
                }
            }

            managerDao.updateProject(map);
            resultBody.setCode(200);
            resultBody.setMessages("修改成功!");
            return  resultBody;
        }catch (Exception e){
            resultBody.setCode(-3434);
            resultBody.setMessages("修改失败!");
            return  resultBody;
        }
    }

}
