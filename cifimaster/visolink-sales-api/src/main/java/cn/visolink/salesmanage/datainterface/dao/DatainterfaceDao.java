package cn.visolink.salesmanage.datainterface.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 数据接口
 *
 * @author 刘昶
 * 接口对接,对t_mm_project表进行更新
 * 因为每天要更新,所以先清空表
 * @return
 * @date 2019-9-20
 */
@Mapper
public interface DatainterfaceDao {

    /**
     * 清空t_mm_project
     *
     * @return
     */
    public void deleteProject();

    Map selectContractBycontractGUID(String contractGUID);

    public Map selectOrderByorderGuid(String orderGuid);

    /**
     * 清空t_mm_staging
     *
     * @return
     */
    public void deleteStaging();

    /**
     * 清空t_mm_group
     *
     * @return
     */
    public void deleteGroup();

    /**
     * 清空t_mm_designBuild
     *
     * @return
     */
    public void deleteDesignBuild();

    /**
     * 新增t_mm_project
     */
    Integer insertSysProject(List<Map> list);

    /**
     * 新增t_mm_staging
     *
     * @return
     */
    Integer insertSysStaging(List<Map> list);

    /**
     * 新增t_mm_group
     *
     * @return
     */
    Integer insertSysGroup(List<Map> list);

    /**
     * 新增t_mm_designBuild
     *
     * @return
     */
    Integer insertSysDesignBuild(List<Map> list);

    /**
     * 查询t_mm_project
     */
    Map selectSysProject(Map map);

    /**
     * 查询t_mm_staging
     *
     * @return
     */
    Map selectSysStaging(Map map);

    /**
     * 查询t_mm_group
     *
     * @return
     */
    Map selectSysGroup(Map map);

    /**
     * 新增t_mm_designBuild
     *
     * @return
     */
    Map selectSysDesignBuild(Map map);

    /**
     * 新增t_mm_designBuild 批量查询
     * 批量查询数据 bql 2020.08.03
     *
     * @param list
     * @return List<String>
     */
     List<String> selectSysDesignBuildBatch(List<Map> list);

    /**
     * 修改t_mm_group
     *
     * @return
     */
    void updateDesignBuild(Map map);


    /**
     * 批量修改t_mm_group
     *  bql 2020.07.28
     *
     * @param list list
     */
    void updateDesignBuildBatch(List<Map> list);

    /**
     * 修改t_mm_group
     *
     * @return
     */
    void updateProject(Map map);

    /**
     * 修改t_mm_staging
     *
     * @return
     */
    void updateStaging(Map map);

    /**
     * 修改t_mm_group
     *
     * @return
     */
    void updateGroup(Map map);


    /**
     * 查询 t_sys_org
     *
     * @return
     */
    List<Map> seleteSysorg();

    /**
     * 查询 t_sys_org里的区域集团的里城市
     * city 城市
     */
    List<Map> seleteCity(String city);

    /**
     * 查询 t_mm_project里面的项目,多条件查询
     *
     * @return
     */
    List<Map> selectProject(Integer cityid, int businessunitid);

    /**
     * 查询t_mm_staging里面的分期
     *
     * @return
     */
    List<Map> selectStaging(String projectID);

    /**
     * 查询分期下的组团
     *
     * @return
     */
    List<Map> selectGroup(String projectid, String projectfid);

    /**
     * 查询业态
     */
    List<Map> selectDesignBuildd(String designbuildid);

    /**
     * 查询面积段
     */
    List<Map> selectArea(String productId);

    /**
     * 新增面积段
     */
    void insertArea(Map map);


    /**
     * 修改区域到月指标状态
     */
    void updateBusinessUnit();

    /**
     * 事业表里添加数据
     */
    void insertbusiness(List<Map> list);

    /**
     * 删除今年的数据
     */
    void deletecontract(String time);

    /**
     * 增加今年的数据
     */
    void insrtconrtact(List<Map<String, Object>> list);

    /**
     * 修改今年的数据
     */
    void updateconrtact();

    /**
     * 修改今年的数据
     */
    void updateconrtactByStartTime(String startTime);

    void updateorderByStartTime(String startTime);

    void updateorderByStartTimeSD(@Param("startTime") String startTime, @Param("endTime") String endTime);

    void updateConrtactByID(List list);

    void updateOrderByID(List list);

    /**
     * 删除今年数据
     */
    void deleteorder(String time);

    /**
     * 增加今年的数据
     */
    void insrtorder(List<Map<String, Object>> list);

    List<Map> getProjectId(String projectId);

    void deleteOrderByTimeSD(@Param("startTime") String startTime, @Param("endTime") String endTime);

    void deleteContractByTime(String startTime, String endTime);

    /**
     * 修改
     */

    void updateorder();

    /**
     * 删除今年的数据
     */
    void deletefyqzkl(String time);

    /**
     * 增加今年的数据
     */
    void insertfyqzkl(List<Map<String, Object>> list);

    /**
     * 增加来人量
     */
    void insertcstnum(List<Map<String, Object>> list);

    /**
     * 查询区域
     *
     * @return
     */
    List<Map> selectquyu();

    /**
     * 查询事业表
     */
    List<Map> selectbusin();

    /**
     * 修改事业表的字段
     */
    void updatabusin(int code);

    /**
     * 添加事业部,区域集团
     */
    void insertbusinessunit(Map map);

    /**
     * 查询 t_mm_project里面的项目,多条件查询
     *
     * @return
     */
    List<Map> selectProjectt(String businessunitid);

    /**
     * 添加项目
     */
    void insertprojects(Map map);

    /**
     * 添加添加分期
     */
    void insertstagings(Map map);


    /**
     * 查询分期下的组团
     *
     * @return
     */
    List<Map> selectGroupp(String projectfid);

    /**
     * 添加组团
     */
    void insertgroups(Map map);

    /**
     * 查询业态
     */
    List<Map> selectDesignBuild(String designbuildid);

    /**
     * 添加业态
     */
    void insertesignbuilds(Map map);

    /**
     * 查询t_mm_staging里面的分期
     *
     * @return
     */
    List<Map> selectStagingg(String projectID);

    /**
     * 定时取金蝶数据
     */
    void insertvalueinput(List<Map<String, Object>> list);

    /**
     * 定时取明源的来人量
     */
    void insertcstnums(List<Map<String, Object>> list);

    /**
     * 增加来人量之前先删掉
     */
    void deletecstnums();

    /**
     * 增量删除前一天数据
     */
    void deleteVisitsByGjDate(String startTime);

    /**
     * 供货增加前先删除
     */
    void deletevalueinput();

    /**
     * 供货增加前先删除
     */
    void deletevalueinputs(String time);


    /**
     * 初始化初始化事业项目关系表
     */
    void delprojectrel();

    void insertprojectrel();

    /**
     * 增加完后进行修改
     */
    void updateprojectrel();

    /**
     * 初始化项目分期关系表先删除
     */
    void deletestagerel();

    /**
     * 初始化项目分期表
     */
    void insertProjectStagerel();


    /**
     * 初始化楼栋产品构成关系表
     */
    void insertGroupDesignbuildrel();


    /**
     * 初始化组团分期关系表
     */
    void insertStageGroup();

    /**
     * 初始化产品组团关系表
     */
    void insertproductgroup();

    /**
     * 初始化产品组团关系表
     */
    void insertproductrel();

    /**
     * 初始化整合先删除
     */
    void deletemainrel();

    /**
     * 初始化整合
     */
    void insertmainrel();

    /**
     * 初始化整合
     */
    void insertproducareatrel();

    /**
     * 初始化组织先删除
     */
    void deleterganization();

    /**
     * 区域
     */
    void insertrganization();

    /**
     * 项目组织
     */

    void insertorganizations();

    /**
     * 删除所有事业部
     */
    void deletebu();


    /**
     * 查询集团确认后的数据
     */

    List<Map> selectsignse(String time);


    /**
     * 发送esb接口数据查询
     */
    List<Map<String, Object>> selectSendGXC(Map params);

    void deleteOrderByUpdateTime(String startTime, String endTime);

    void deleteContractByUpdateDate(String startTime, String endTime);

    /**
     * 初始化签约数据
     */
    void initializationSingingData(Map map);

    public void deleteordertwo();


    /*
    * 增量删除
    *
    * */

    void deleteOrderByTime(@Param("startTime") String startTime,@Param("endTime") String endTime);

    List<Map<String, String>> getGXCData();

    /**
     * 初始化月度标识
     */
    int initMonthProjectType();


    int delRepeatDataBusiness();

    int delRepeatDataBusinessRel();

    /**
     * 初始化首开标识
     */
    int initFirstPlanProjectType();

    /**
     * 初始化操盘方式
     * */
    int initProjectCpType();
    /**
     * 初始化是否营销主操
     * */
    int initProjectYxzc();


}
