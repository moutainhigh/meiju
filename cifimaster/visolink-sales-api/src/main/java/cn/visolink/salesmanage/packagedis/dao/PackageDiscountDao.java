package cn.visolink.salesmanage.packagedis.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Auther: Wjc
 * @Date: 2019/10/11 0011 16:08
 * @Description:一揽子折扣
 */
public interface PackageDiscountDao {
    /**
     * 查询登录人下的项目
     * @param map
     * @return
     */
    List<Map> getProjects(Map map);

    /**
     * 查询项目的业态
     * @param map
     * @return
     */
    List<Map> getFormats(Map map);

    /**
     * 查询项目的楼栋
     * @param map
     * @return
     */
    List<Map> getBuildData(Map map);
    /**
     * 获取项目名称
     */
    Map getProjectName(Map map);
    /**
     * 添加一揽子折扣
     * @param map
     * @return
     */
    int insertPackageDis(Map map);

    /**
     * 添加一揽子折扣详情
     * @param map
     * @return
     */
    int insertPackageDiscountItem(Map map);

    /**
     * 添加一揽子分期
     * @param map
     * @return
     */
    int insertPackageStages(Map map);

    /**
     * 添加一揽子分期详情
     * @param map
     * @return
     */
    int insertPackageStagesItem(Map map);

    /**
     * 添加楼栋
     * @param map
     * @return
     */
    int insertPackageBuilding(Map map);
    /**
     * 修改一揽子折扣
     * @param map
     * @return
     */
    int updatePackageDis(Map map);

    /**
     * 修改一揽子折扣详情
     * @param map
     * @return
     */
    int updatePackageDiscountItem(Map map);
    /**
     * 修改一揽子分期
     * @param map
     * @return
     */
    int updatePackageStages(Map map);

    /**
     * 修改一揽子分期
     * @param map
     * @return
     */
    int updatePackageStagesItem(Map map);

    /**
     * 修改楼栋
     * @param map
     * @return
     */
   int updatePackageBuilding(Map map);

    /*
    获取一揽子分期主数据
    */
    Map getPackageStagesApply(Map map);
    /**
     * 获取一揽子分期详细数据
     */
    List<Map> getPackageStagesItemApply(Map map);

    /**
     * 获取一揽子分期楼栋数据
     */
    List<Map> getPackageStageBuildIng(Map map);
    /**
     * 获取一揽子折扣主数据
     */
    Map getPackageDisApply(Map map);

    /**
     * 获取一揽子折扣详细数据
     */
    List<Map> getPackageDisImemApply(Map map);

    //清空一揽子折扣详细数据
    void deletePackageDisItem(Map map);
    /**
     *删除楼栋信息
     */
    void deleteBuildingData(Map map);

    ///删除一揽子分期详细数据
    void deletePackageItem(Map map);

    /**
     * 根据流程id查询主数据id
     *
     */
    Map getBaseId(Map map);

    /**
     * 清空一揽子分期数据
     */
    void clearPackageStages(@Param(value = "baseID")String baseID);

    /**
     * 清空一揽子折扣数据
     */
    void clearPackageDis(@Param(value = "baseID")String baseID);
    //清空流程数据
    void deleteFlowData(@Param(value = "id")String id);
    void createFlowData(Map map);
    void updateFlowData(Map map);
    void clearFlowData(@Param(value = "baseID")String baseID);



    /**
     * 一揽子分期列表
     *
     */
    List<Map> stagesSelect(Map map);

    String getFlowCode(@Param(value = "jsonid") String jsonid);

    /*

   查询流程数据
     */
    Map getFlowDataInfo(@Param(value = "jsonId")String param);
    //查询窗口期
    List<Map> getWindowPashDate(Map map);
}
