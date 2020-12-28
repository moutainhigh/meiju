package cn.visolink.salesmanage.datainterface.service;

import cn.visolink.exception.ResultBody;

import java.util.List;
import java.util.Map;

public
interface Datainterfaceservice {
    /**
     * 新增t_mm_project
     */
    Map insertProject(Map bats);
    /**
     * 新增t_mm_staging
     * @return
     */
    Map insertStaging(Map bats);

    ResultBody initMingyuanOrder();

    void initOrderAndSignNull();
    /**
     * 新增t_mm_project
     */

    /**
     * 新增t_mm_group
     * @return
     */
    Map insertGroup(Map bats);

    /**
     * 新增t_mm_designBuild
     * @return
     */
    Map insertDesignBuild(Map bats);

    /**
     * 查询 t_sys_org
     * @return
     */
    List<Map> seleteSysorg();
    /**
     * 查询 t_sys_org里的区域集团目录下的城市
     * @return
     */
    List<Map> selectCity(String city);
    /**
     * 根据城市或者区域id查询t_mm_project里面的数据
     * @return
     */
    List<Map>selectProject(Integer cityid ,int businessunitid);

    /**
     * 根据项目id查询t_mm_staging里面的数据
     * @return
     */
    List<Map>selectStaging(String id);

    /**
     * 根据项目id查询t_mm_Group里面的数据
     * @return
     */
    List<Map>selectGroup(String projectid,String projectfid);


    /**
     * 根据业态楼栋id查询t_mm_designbuild里面的数据
     * @return
     */
    List<Map> selectdesignbuild(String designbuild);
    /**
     * 事业表里添加数据
     * @return
     */

    void  insertbusiness(List<Map> list);


    /**
     * 初始化明源的三张表
     */
    void mingyuan();

    void updataBusinsee();

    /**
     * 初始化idm的表
     *
     */
    void initmmidm();

    /**
     * 查询相关的数据存到明源中
     */
    void  selectsignset(String time);

    /**
     * 发送esb接口数据查询
     */
    Map selectSendGXC(Map params);

    /**
     *来人量
     */
   void initlairenliang(String time);

    ResultBody intiOrderAddData(Map map);
    /*
    * 手动同步
    * */
    ResultBody intiOrderAddDataSD(Map map);

    /**
     * 增量同步明源认购数据
     * */
    void intiOrderByProject(Map map);
    /**
     * 来人量
     */
    ResultBody insertVisllAdd(Map map);

    void initmingyuan(String startTime,String endTime);
    /**
     * 初始化签约信息
     */
    ResultBody intiSingData(Map map);

    //增量更新签约数据
    ResultBody intiSingAddData(Map map);

    ResultBody intiSingDataAll();

    /**
     *
     * @param addYear 开始年份
     * @param nowYear 当前年份
     * @return
     */
    ResultBody intiSingDataMonth(int addYear,int nowYear);

    void initTwoYearOrder();

    ResultBody initMingYuanTwo();

    void initSignByStart(String startTime, String endTime);

    /**
     * 初始化项目标识
     * */
    int initProjectType();

    /**
    * 删除重复数据
     *
    * */
    int delRepeatData();
}
