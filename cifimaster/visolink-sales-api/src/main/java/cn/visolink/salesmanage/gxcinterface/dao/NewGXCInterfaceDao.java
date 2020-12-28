package cn.visolink.salesmanage.gxcinterface.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Mapper
@Repository
public interface NewGXCInterfaceDao {

        /**
         * 供货视图插入
         */
        int insertvaluegh(List<Map<String, Object>> list);

        /*
        * 库存写入
        * */
        int insertReportValue(List<Map<String, Object>> list);
        /**
         * 供货视图插入
         */
        int deleteSignPlan(@Param("startTime") String startTime,@Param("projectId") String projectId);

        int deleteSupplyPlanVersionId(@Param("startTime") String startTime,@Param("projectId") String projectId);

        int deleteReportValue(@Param("startTime") String startTime,@Param("projectId") String projectId);

        int deleteReportValueAll(@Param("startTime") String startTime);
        /**
         * 供货视图删除数据
         */
        int deletegh();

        /**
         * 根据版本ID删除动态货值
         */
        int deleteDynamicValue(@Param("startTime") String startTime, @Param("projectId") String projectId);

        /**
         * 动态货值视图插入
         */
        int insertvaluedthz(List<Map<String, Object>> list);

        /**
         * 动态货值视图删除数据
         */
        int deletedthz();

        /**
         * 战规货值视图插入
         */
        int insertvaluezghz(List<Map<String, Object>> list);

        /**
         * 删除
         * @param startTime
         * @return
         */
        int deletePlanValueByVersionId(String startTime);

        /**
         * 战规货值视图删除数据
         */
        int deletezghz();

        /**
         * 签约视图插入
         */
        int insertvalueqy(List<Map<String, Object>> list);

        /**
         * 签约视图删除数据
         */
        int deleteqy();

        /**
         * 明源与供销存面积段归集,修改库存可售，正常非车位的
         */
        int updateAvailableStock();

        /**
         * 明源与供销存面积段归集,修改库存可售，车位的
         */
        int updateAvailableStockNotCar();




        /**
         * 初始化初始化事业项目关系表
         */
        void insertprojectrel(String projectId);

        /**
         * 增加完后进行修改
         */
        void updateprojectrel(String projectId);

        /**
         * 初始化项目分期表
         */
        void insertProjectStagerel(String projectId);


        /**
         * 根据项目ID获取组团ID
         * */
        List<Map> getGroupIdByProjectId(String projectId);
        /**
         * 根据项目ID获取业态ID
         * */
        List<Map> getProductIdByProjectId(String projectId);

        /**
         * 根据项目ID获取面积段ID
         * */
        List<Map> getHouseIdByProjectId(String projectId);
        List<Map> getStageIdByProjectId(String projectId);
        /**
         * 根据项目ID获取区域ID
         * */
        String getQyIdByProjectId(String projectId);
        /**
         * 初始化组团分期关系表
         */
        void insertStageGroup(String groupId);

        /**
         * 初始化楼栋产品构成关系表
         */
        void insertGroupDesignbuildrel(String groupId);

        /**
         *初始化产品组团关系表
         */
        void insertproductgroup(String groupId);

        /**
         *初始化产品组团关系表
         */
        void insertproductrel(String resProductId);

        /**
         * 初始化整合
         */
        void insertproducareatrel(String groupId);


        /**
         * 初始化整合
         */
        void insertmainrel(String projectId);


        /**
         * 查询区域
         * @return
         */
        List<Map> selectquyu(String projectId);

        /**
         * 删除所有事业部
         */
        void deletebu(@Param("projectId") String projectId,@Param("resGroupId")String resGroupId,@Param("resProductId")String resProductId,@Param("resHouseId")String resHouseId,@Param("resStageId")String resStageId,@Param("qyId")String qyId);


        /**
         * 添加事业部,区域集团
         *
         */
        void  insertbusinessunit(Map map);

        /**
         * 查询 t_mm_project里面的项目,多条件查询
         * @return
         */
        List<Map> selectProjectt(@Param("businessunitid") String businessunitid, @Param("projectId") String projectId);

        /**
         * 添加项目
         *
         */
        void  insertprojects(Map map);

        /**
         * 查询t_mm_staging里面的分期
         * @return
         */
        List<Map> selectStagingg(String projectID);

        /**
         * 添加添加分期
         *
         */
        void  insertstagings(Map map );

        /**
         * 查询分期下的组团
         * @return
         */
        List<Map> selectGroupp(@Param("projectfid") String projectfid,@Param("projectId") String projectId);

        /**
         * 添加组团
         *
         */
        void  insertgroups(Map map);

        /**
         * 查询业态
         *
         */
        List<Map> selectDesignBuildd(@Param("group_code")String designbuildid,@Param("projectId") String projectId);


        /**
         * 添加业态
         *
         */
        void  insertesignbuilds(Map map);

        /**
         * 查询面积段
         *
         */
        List<Map> selectArea(@Param("productId") String productId,@Param("projectId") String projectId);

        /**
         * 新增面积段
         *
         */
        void insertArea(Map map);

        /**
         * 修改区域到月指标状态
         *
         */
        void updateBusinessUnit();

        /**
         * 初始化签约数据
         */
        void initializationSingingData(Map map);






        /**
         * 获取事业部数据
         * @param map
         * @return
         */
        int getBusiness(Map map);

        int deleteBasic(Map map);
        int deleteBasicIndex(Map map);

        /**
         * 修改区域前三月的成交率
         * @param map
         * @return
         */
        int updateRegionThreeMonth(Map map);

        /**
         * 修改区域前一月的成交率
         * @param map
         * @return
         */
        int updateRegionOneMonth(Map map);

        /**
         * 修改项目前三月的成交率
         * @param map
         * @return
         */
        int updateProjectThreeMonth(Map map);

        /**
         * 修改项目前一月的成交率
         * @param map
         * @return
         */
        int updateProjectOneMonth(Map map);

        /**
         * 修改集团前三月的成交率
         * @param map
         * @return
         */
        int updateGroupThreeMonth(Map map);

        /**
         * 修改集团前一月的成交率
         * @param map
         * @return
         */
        int updateGroupOneMonth(Map map);

        /**
         * 修改集团的年度计划签约
         *
         * @return
         */
        int updateGroupYearSign(Map map);
        /**
         * 修改区域的年度计划签约
         *
         * @return
         */
        int updateRegionYearSign(Map map);
        /**
         * 修改项目的年度计划签约
         *
         * @return
         */
        int updateProjectYearSign();

        /**
         * 修改集团供货值
         * @param map
         * @return
         */
        int updateGroupSupply(Map map);

        /**
         * 修改区域供货值
         * @param map
         * @return
         */
        int updateRegionSupply(Map map);

        /**
         * 修改项目供货值
         * @param map
         * @return
         */
        int updateProjectSupply(Map map);

        /**
         *修改分期供货值
         * @param map
         * @return
         */
        int updateInstallmentSupply(Map map);

        /**
         * 修改组团供货值
         * @param map
         * @return
         */
        int updateGroupcodeSupply(Map map);

        /**
         * 修改业态供货值
         * @param map
         * @return
         */
        int updateProduceSupply(Map map);
        /**
         * 查询月度计划表中是否存在这个月份
         * @param map
         * @return
         */
        List<Map> selectPlanMonth(Map map);
        /**
         * 集团月度计划查询
         * @param map
         * @return
         */
        List<Map> getGroupMonthPlan(Map map);

        /**
         * 添加月度计划主表
         * @param map
         * @return
         */
        int deleteMonthPlan(Map map);
        int insertMonthPlan(Map map);
        /**
         * 添加月度计划
         * @param map
         * @return
         */
        int insertMonthPlanBasis(Map map);

        /**
         * 添加月度计划指标
         * @param map
         * @return
         */
        int insertMonthPlanIndex(Map map);

        /**
         * 查询集团月度计划的区域数据
         * @param map
         * @return
         */
        List<Map> getGroupAllMessage(Map map);
        /**
         * 查询集团下子级的数据
         * @param map
         * @return
         */
        List<Map> getGroupChildMessage(Map map);

        /**
         * 暂存，下达，上报 （修改状态）
         * @param map
         * @return
         */
        int updatePlanStatus(Map map);

        /**
         * 修改状态 （生效未生效）
         * @param map
         * @return
         */
        int updateIsEffevtive(Map map);
        /**
         * 区域月度计划查询
         * @param map
         * @return
         */
        List<Map> getRegionalMonthPlan(Map map);

        /**
         * 获取集团给区域下的数据
         * @param map
         * @return
         */
        int getGroupReleaseRegional(Map map);

        /**
         * 获取区域的合计
         * @param map
         * @return
         */
        int getRegionalAggregate(Map map);
        /**
         * 在区域里获取集团下达数据
         * @param map
         * @return
         */
        List<Map> getGroupReleaseInRegional(Map map);
        /**
         *根据区域id获取事业部数据
         * @param map
         * @return
         */
        List<Map> getBusinessForRegional(Map map);

        /**
         * 根据项目id获取事业部数据
         * @param map
         * @return
         */
        List<Map> getBusinessForProject(Map map);

        /**
         *添加区域月度计划指标
         * @param map
         * @return
         */
        int insertRegionalMonthPlanIndex(Map map);

        /**
         * 获取区域初始化数据
         * @param map
         * @return
         */
        List<Map> getRegionalMessage(Map map);

        /**
         * 判断区域月份是否存在
         * @param map
         * @return
         */
        int selectRegionalMonth(Map map);

        /**
         * 判断项目月份是否存在
         * @param map
         * @return
         */
        int selectProjectMonth(Map map);
        /**
         * 区域指标细化
         * @param map
         * @return
         */
        List<Map> getRegionChildMessage(Map map);

        /**
         * 项目月度计划查询
         * @param map
         * @return
         */
        List<Map> getProjectMonthPlan(Map map);

        /**
         * 从项目月度计划获取区域下达数据
         * @param map
         * @return
         */
        List<Map> getRegionalReleaseInProject(Map map);
        /**
         *添加项目月度计划指标
         * @param map
         * @return
         */
        int insertProjectMonthPlanIndex(Map map);

        /**
         * 获取项目初始化数据
         * @param map
         * @return
         */
        List<Map> getProjectMessage(Map map);


        /**
         *
         * @param
         * @return
         */
        Map getProjectAreaID(String projectId);
        /**
         * 查询区域下项目未上报的条数
         * @param map
         * @return
         */
        int getRegionUnderProject(Map map);

        /**
         * 获取集团下区域未上报的条数
         * @param map
         * @return
         */
        int getGroupUnderRegion(Map map);

        /**
         *修改集团的状态
         * @param map
         * @return
         */
        int updateGroupPlanStatus(Map map);

        /**
         * 修改区域的费用
         * @param map
         * @return
         */
        int updateRegionFunds(Map map);

        /**
         * 修改项目的费用
         * @param map
         * @return
         */
        int updateProjectFunds(Map map);

        /**
         * 修改集团的明源数据
         * @param map
         * @return
         */
        int updateGroupmy(Map map);

        /**
         * 修改区域的明源数据
         * @param map
         * @return
         */
        int updateRegionmy(Map map);

        /**
         * 修改为空的值
         * @param map
         * @return
         */
        int updateIsnull(Map map);


        /**
         * 修改项目供货值、套数、版本
         * @param map
         * @return
         */
        int updateProjectValue(Map map);
        int updateTotalPlan(Map map);
        /**
         * 修改组团供货值、套数、版本
         * @param map
         * @return
         */
        int updateGroupcodeValue(Map map);

        /**
         * 修改分期供货值、套数、版本
         * @param map
         * @return
         */
        int updateInstallmentValue(Map map);

        /**
         * 修改业态供货值、套数、版本
         * @param map
         * @return
         */
        int updateProductValue(Map map);

        /**
         * 修改面地段供货值、套数、版本
         * @param map
         * @return
         */
        int updateHousePackageValue(Map map);

        /**
         * 修改区域供货值、套数、版本
         * @param map
         * @return
         */
        int updateAreaValue(Map map);

        /**
         * 修改集团供货值、套数、版本
         * @param map
         * @return
         */
        int updateGroupValue(Map map);


        /**
         * 初始化年度签约金额
         */
        void initYearPlanSignData(Map map);
        /*
         * 通过集团状态确认是否可以跳供消存
         * */
        List<Map> getGroupMonthPlanForProject(Map map);

        Integer updateMonthlyPlanBasis(Map map);

}
