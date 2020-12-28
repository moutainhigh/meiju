package cn.visolink.salesmanage.pricing.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/12 11:44 上午
 */
@Mapper
@Repository
public interface PricingMapper {

    /**
     * 保存明源的数据到本系统
     *
     * @param map
     */
    void saveData(Map map);

    void updateData(Map map);

    /*

   查询明源推送的数据是否已存在
     */
    Map findJonisExist(Map map);

    /**
     * 保存定调价主数据
     */
    void insertDecideMasterData(Map map);

    void updateDecideMasterData(Map map);

    /**
     * 获取明远推送的json数据
     */

    Map getApplyData(Map map);

    /**
     * 获取本系统定调价数据
     *
     * @param map
     * @return
     */
    Map getPricingBaseData(Map map);

    /*
    获取定调价字典数据
     */
    List<Map> getPricingDictoryList(Map map);

    /**
     * 清除定调价结果及价格对比数据
     */
    void clearPricingResultData(Map map);

    /**
     * 清除 货值压力测算 和 货值偏差说明
     *
     * @param map map
     */
    void clearPressureCompute(Map map);

    /**
     * 清除 价格偏离度
     *
     * @param map map
     */
    void clearPriceDeviation(Map map);

    /**
     * 添加定调价价格对比数据
     */
    void insertPricingComparison(Map map);

    /**
     * 添加定调价调价结果数据
     */
    void insertPricingResult(Map map);

    /**
     * 添加 货值压力测算
     *
     * @param map map
     */
    void insertPressureCompute(Map map);

    /**
     * 添加 价格偏离度
     *
     * @param map map
     */
    void insertPriceDeviation(Map map);

    /**
     * 添加 货值偏差说明
     *
     * @param map map
     */
    void insertDeviationExplain(Map map);

    /**
     * 获取价格对比数据
     */
    List<Map> getPriceComparison(@Param(value = "id") String id);

    /**
     * 获取定调价结果数据
     */
    List<Map> getPricingResult(@Param(value = "id") String id);

    /**
     * 获取 货值压力测算
     *
     * @param id id
     * @return return
     */
    List<Map> getPressureCompute(@Param(value = "id") String id);

    /**
     * 获取 价格偏离度
     *
     * @param id id
     * @return return
     */
    List<Map> getPriceDeviation(@Param(value = "id") String id);

    /**
     * 获取 货值偏差说明
     *
     * @param id id
     * @return return
     */
    Map getDeviationExplain(@Param(value = "id") String id);

    /**
     * 获取流程id
     */
    Map getFlowId(@Param(value = "json_id") String json_id);

    /**
     * 上传文件
     */
    public void saveFile(Map map);

    public void updateFileBaseId(Map map);

    public List<Map> getFileList(@Param(value = "id") String id);

    //修改流程数据主数据
    public void updateFlowBaseId(Map paramMap);

    //修改流程数据
    public void updateFlowData(Map paramMap);

    //获取项目信息
    public Map getProjectData(@Param(value = "project_code") String project_code);

    //删除文件
    public Integer deleteFile(@Param(value = "id") String id);

    //清空OA审批流程数据
    public void clearFlowData(@Param(value = "id") String id);

    //清空主数据
    public void clearZhuData(@Param(value = "id") String id);

    /**
     * 根据项目id，获取 累计已签约部分 货值
     */
    Map<String, Object> getAddSignValueByProjectId(Map map);

    /**
     * 价格监测，获取主数据
     *
     * @param map map
     * @return return
     */
    @Select("SELECT * FROM mm_ap_set_the_pricing WHERE id = #{map.baseId}")
    Map<String, Object> priceMonitor(@Param("map") Map map);

    /**
     * 价格监测，获取 上月累计签约金额
     *
     * @param map map
     * @return return
     */
    /*@Select("SELECT t1.Code,CAST(IFNULL(SUM(t1.金额), 0) AS DECIMAL(20,2)) AS cumulativeContractAmount \n" +
            "FROM (SELECT SUM(c.CjRmbTotal) AS 金额,CONCAT_WS( '-', d.productCode, d.freeType, d.businessType, d.proType) AS Code \n" +
            "FROM VS_XSGL_CONTRACT c LEFT JOIN t_mm_designbuild d ON c.x_bldprdId = d.bldPrdID \n" +
            "WHERE IFNULL(c.CloseReason, '') NOT IN ('撤销签约','nos退房','补差') \n" +
            "AND c.x_productType NOT LIKE '车位%' AND c.KINGDEEPROJECTID = #{map.projectId} \n" +
            "AND c.YwgsDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) GROUP BY Code \n" +
            "UNION ALL SELECT -1 * SUM(c.CjRmbTotal) AS 金额,CONCAT_WS( '-', d.productCode, d.freeType, d.businessType, d.proType) AS Code \n" +
            "FROM VS_XSGL_CONTRACT c LEFT JOIN t_mm_designbuild d ON c.x_bldprdId = d.bldPrdID \n" +
            "WHERE IFNULL(c.CloseReason, '') IN('nos退房','退房','换房') AND c.KINGDEEPROJECTID = #{map.projectId} \n" +
            "AND c.YwgsDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) \n" +
            "AND c.CloseDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) \n" +
            "AND c.x_productType NOT LIKE '车位%' GROUP BY Code  \n" +
            "UNION ALL SELECT SUM(c.BcTotal) AS 金额,CONCAT_WS( '-', d.productCode, d.freeType, d.businessType, d.proType) AS Code \n" +
            "FROM VS_XSGL_CONTRACT c LEFT JOIN t_mm_designbuild d ON c.x_bldprdId = d.bldPrdID \n" +
            "WHERE c.Status = '激活' AND c.AuditDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) \n" +
            "AND c.x_productType NOT LIKE '车位%' AND c.KINGDEEPROJECTID = #{map.projectId} \n" +
            "GROUP BY Code) AS t1 \n" +
            "GROUP BY t1.Code ORDER BY t1.Code")
    List<Map<String, Object>> getCumulativeContractedAmountLastMonth(@Param("map") Map map);*/
    @Select("SELECT CAST(IFNULL(SUM(t1.金额), 0) AS DECIMAL(20,2)) AS cumulativeContractAmount \n" +
            "FROM (SELECT SUM(c.CjRmbTotal) AS 金额 FROM VS_XSGL_CONTRACT c \n" +
            "WHERE IFNULL(c.CloseReason, '') NOT IN ('撤销签约','nos退房','补差') \n" +
            "AND c.x_productType NOT LIKE '车位%' AND c.KINGDEEPROJECTID = #{map.projectId} \n" +
            "AND c.YwgsDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) \n" +
            "UNION ALL SELECT -1 * SUM(c.CjRmbTotal) AS 金额 FROM VS_XSGL_CONTRACT c \n" +
            "WHERE IFNULL(c.CloseReason, '') IN('nos退房','退房','换房') AND c.KINGDEEPROJECTID = #{map.projectId} \n" +
            "AND c.YwgsDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) \n" +
            "AND c.CloseDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) \n" +
            "AND c.x_productType NOT LIKE '车位%' \n" +
            "UNION ALL SELECT SUM(c.BcTotal) AS 金额 FROM VS_XSGL_CONTRACT c \n" +
            "WHERE c.Status = '激活' AND c.AuditDate < DATE_ADD(CURDATE(), INTERVAL - DAY (CURDATE()) + 1 DAY) \n" +
            "AND c.x_productType NOT LIKE '车位%' AND c.KINGDEEPROJECTID = #{map.projectId} \n" +
            ") AS t1 ")
    Map<String, Object> getCumulativeContractedAmountLastMonth(@Param("map") Map map);

    /**
     * 获取 年初定稿版整盘货值
     *
     * @param map map
     * @return return
     */
    @Select("SELECT project_value AS wholePlateInvestmentValue FROM `mm_ap_set_project_value` \n" +
            "WHERE project_id IS NOT NULL AND project_id != '' AND project_id = #{map.projectId}")
    Map<String, Object> getWholePlateInvestmentValue(@Param("map") Map map);

    /**
     * 获取主数据分期编号
     *
     * @param map map
     * @return return
     */
    @Select("SELECT DISTINCT stageCode FROM `t_mm_staging` WHERE isDelete = 0 AND kingdeeProjectID = #{map.projectId}")
    List<Map> getStageCode(@Param("map") Map map);
}
