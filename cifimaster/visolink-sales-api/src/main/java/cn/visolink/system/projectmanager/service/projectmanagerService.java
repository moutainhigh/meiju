package cn.visolink.system.projectmanager.service;

import cn.visolink.exception.ResultBody;
import org.apache.ibatis.mapping.ResultMap;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface projectmanagerService {
    /**
     * 项目管理查询
     * */
    public  Map projectListSelect(Map<String, Object> map);


    /**
     * 项目管理导出
     * */
    public List<Map> exportProject(Map map);
    /**
     * 启用禁用项目
     * */
    public  Integer  projectIsEnableUpdate(Map<String, Object> map);
    /**
     * 删除项目
     * */
    public  Integer  projectDeleteUpdate(Map<String, Object> map);
    /*
     * 判断项目编号是否已存在
     * */
    public  Map <String, Object> projectNumIsExsit(Map<String, String> dataMap);
    /*
     * 增加一条新的项目
     * */
    public Integer addNewProjectInfoInsert(Map<String, String> projectMap);
    /*
     * 与售前项目相关联,和AddNewProjectSaleRelInsert为一条事务
     * */
    public Integer addNewProjectSaleRelUpdate(Map<String, String> projectMap);
    /*
     * 与售前项目相关联,和AddNewProjectSaleRelUpdate为一条事务
     * */
    public Integer addNewProjectSaleRelInsert(Map<String, String> projectMap);

    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是查找项目ID
     * */
    public  Map <String, String> newProjectOrgSelect(Map<String, String> projectMap);
    /*
     * 项目关联组织, 先找到項目ID，再跟新项目，这里是跟新项目
     * */
    public  Integer newProjectOrgUpdate(Map<String, String> projectMap);
    /*
     * 修改项目信息
     * */
    public  Integer projectInfoModify(Map<String, String> projectMap);

    /*
     * 判断当前是否已经存在过关联关系
     * */
    public Map <String, Object>  projectSaleRelCountBySaleProjectIdSelect(Map<String, String> projectMap);

    /*
     * 若产生关联关系（>0）且则走这一条方法
     * */
    public Integer newProjectSaleRelNoDel(Map<String, String> projectMap);

    /*
     * 增加项目和修改项目的调用方法
     * */
    public String projectexecute(Map<String, String> dataMap, HttpServletRequest request, HttpServletResponse response);

    /*
     * 增加项目
     * */
    public Map <String, Object> addNewProjectInfo(@RequestBody Map<String, String> projectMap);

    /*
     * 添加完成修改对应组织的项目ID,查询当前组织的项目ID
     * */
    public void updateOrgProject(Map<String, String> projectMap);

    /*
     * 修改项目信息,该方法要被controller层调用，集合需要修改项目信息方法的接口和逻辑
     * */
    public  Map <String, Object> updateProjectInfo(@RequestBody Map<String, String> projectMap);

    /*
     * 查询单条项目的数据
     * */
    public  List<Map<String,Object>> selectOneProject(Map<String, Object> map);

    /*
    * 修改参数的状态
    * */
    public  Integer  updateMenuStatus(Map<String, Object> map);

    /*
    * 修改systemmenus信息
    * */
    public Map systemmenus(Map map);

    public List getWglProject(Map map);

    //删除主数据已经删除的项目
    public int delPRoject();
    public ResultBody addGlProject(Map map);
    public ResultBody updateProject(Map map);
    }
