package cn.visolink.salesmanage.gxcinterface.dao;

import cn.visolink.exception.ResultBody;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据接口
 *
 * @author 刘昶
 *    接口对接,对t_mm_project表进行更新
 *    因为每天要更新,所以先清空表
 *
 *    @return
 *
 * @date 2019-9-20
 */

@SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
@Mapper
@Repository
public interface GXCInterfaceDao {


        /**
        * 库存写入
        * */

        int insertReportValue(List<Map<String, Object>> list);

        int deleteReportValue(@Param("startTime") String startTime);

        /**
         * 供货视图插入
         */
        int insertvaluegh(List<Map<String, Object>> list);

        int delSupply();

        /**
        * 查询所有分期
        * */
        List<Map> getStageList(String project_id);





        /**
        * 获取确认版确认时间
        * */
        String getQrDate(String stageId);

        /**
         * 获取定稿版确认时间
         * */
        String getDgDate(String stageId);

        /**
        * 删除最新版以外其他版本
        * */
        int delNewOtjerVersion(@Param("stageId") String stageId);

        /**
        * 删除确认版以外其他版本
        * */
        int delQrOtherVersion(@Param("stageId") String stageId,@Param("qrDate") String qrDate);

        /**
         * 删除定稿版以外其他版本
         * */
        int delDgOtherVersion(@Param("stageId") String stageId,@Param("qrDate") String qrDate);

        /**
         * 供货视图插入
         */
        int deleteSignPlan(String startTime);

        int deleteSupplyPlanVersionId(String startTime);
        /**
         * 供货视图删除数据
         */
        int deletegh();

        /**
         * 根据版本ID删除动态货值
         */
        int deleteDynamicValue(String startTime);

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


}
