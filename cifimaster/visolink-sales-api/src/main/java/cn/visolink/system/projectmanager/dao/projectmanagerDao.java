package cn.visolink.system.projectmanager.dao;


import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 孙林
 * @date:2019-9-10
 * */
public interface projectmanagerDao {
  /**
   * 项目管理查询
   * */
  public  List<Map<String,Object>> projectListSelect(Map<String, Object> map);

  /**
   * 项目管理导出
   * */
  public  List<Map<String,Object>> exportProject(Map<String, Object> map);

  public  Integer projectListSelectCount(Map<String, Object> map);
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
    *
    * */
    public  Map <String, Object> projectNumIsExsit(Map<String, String> dataMap);

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
   * 增加一条新的项目
   * */
  public Integer addNewProjectInfoInsert(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateProjectorgrel(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateOne(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateTwo(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateThree(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateFour(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateFive(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateSix(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateSeven(Map<String, String> projectMap);
  /*
   * 该接口和增加一条新的项目一起执行
   * */
  public Integer addNewProjectSaleRelUpdateEight(Map<String, String> projectMap);

  /*
  * 查询单条项目的数据
  * */
  public  List<Map<String,Object>> selectOneProject(Map<String, Object> map);

  /*
  * 修改菜单状态
  * */
  Integer updateMenuStatus(Map<String, Object> map);

  /*修改Menus信息*/
  Map systemMenuOldPathSelect();

  Map systemMenuNewPathSelect();

  Integer systemMenuInfoUpdate();

  List<Map> getWglProject(Map map);

  int delProject();
  void addGlProject(Map map);
  void updateProject(Map map);

}
