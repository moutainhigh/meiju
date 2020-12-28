package cn.visolink.firstplan.receipt.dao;

import cn.visolink.exception.ResultBody;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金付款单 Mapper接口
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */

@Mapper
@Repository
public interface ReceiptDao {

    /**
     * 添加付款单
     *
     * @param map map
     * @return int
     */
    int initReceipt(Map map);

    /**
     * 添加付款单明细
     *
     * @param list list
     * @param map map
     * @return int
     */
    int initReceiptDetail(@Param("list")List list,@Param("map")Map map);

    /**
     * 付款单明细风控信心查询
     *
     * @param map map
     * @return int
     */
    List<Map> selectOrderFk(Map map);

    /**
     * 付款单明细风控信心查询
     *
     * @param map map
     * @return int
     */
    Integer selectProgectFk(Map map);

    /**
     * 付款单查询
     *
     * @param map map
     * @return int
     */
    List<Map<String,String>> selectReceipt(Map map);


    /**
     * 待付款单查询
     *
     * @param map map
     * @return int
     */
    List<Map<String,String>> selectWaitReceipt(Map map);


    /**
     * 佣金台账查询
     *
     * @param map map
     * @return int
     */
    List<Map<String,String>> selectCommissionStanding(Map map);

    /**
     * 佣金台账查询导出
     *
     * @param map map
     * @return int
     */
    List<Map<String,String>> selectExcelCommissionStanding(Map map);

    /**
     * 付款单查询
     *
     * @param map map
     * @return int
     */
    List<Map<String,String>> selectExcelWaitReceipt(Map map);



    /**
     * 付款单明细查询
     *
     * @param map map
     * @return int
     */
    List<Map<String,Object>> selectExcelWaitReceipt2(Map map);

    /**
     * 付款单明细查询
     *
     * @param map map
     * @return int
     */
    List<Map<String,Object>> selectReceiptDetail(Map map);

    /**
     * 付款单明细导出查询
     *
     * @param map map
     * @return int
     */
    List<Map<String,Object>> selectExcelReceiptDetail(Map map);

    /**
     * 付款单状态修改
     *
     * @param map map
     */
    void updatePaymentStatus(Map map);

    /**
     * 付款单状态修改
     *
     * @param map map
     */
    void updatePaymentStatus2(Map map);

    /**
     * 审批流付款单状态修改
     *
     * @param map map
     */
    void updatePaymentStatus3(Map map);

    /**
     * 付款单申请时间修改
     *
     * @param map map
     */
    void updateApplicationTime(Map map);

    /**
     * 付款单付款通过修改金额（加）
     *
     * @param map map
     */
    void updateAmountClosed(Map map);

    /**
     * 付款单付款驳回修改金额（减）
     *
     * @param map map
     */
    void updateAmountClosed2(Map map);

    /**
     * 付款单明细付款金额修改
     *
     * @param map map
     * @return Integer
     */
    Integer updateReceiptDetail(Map map);

    /**
     * 付款单明细付款金额修改
     *
     * @param list list
     * @param userName userName
     * @return Integer
     */
    Integer updateReceiptDetailList(@Param("list")List<Map<String,Object>> list,@Param("userName")String userName);


    /**
     * 付款单付款金额修改
     *
     * @param map map
     * @return Integer
     */
    Integer updateReceiptAmount(Map map);

    /**
     * 付款单付款金额修改
     *
     * @param map map
     * @return Integer
     */
    Integer updateReceiptAmountAll(Map map);

    /**
     * 获取 当期日期，最大的核算单编号
     *
     * @param checklistCodeDate checklistCodeDate
     * @return return
     */
    @Select("SELECT MAX(receipt_code) FROM `cm_receipt` WHERE isdel = 0 \n" +
            "AND create_time >= DATE_ADD(#{checklistCodeDate},INTERVAL 0 DAY) AND create_time < DATE_ADD(#{checklistCodeDate},INTERVAL 1 DAY)")
    String getMaxReceipCode(@Param("checklistCodeDate") String checklistCodeDate);


    /**
     * 查询当前付款单审批状态
     *
     * @param map map
     * @return list
     */
    List<Map<String, Object>> getPaymentStatus(Map map);

    /**
     * 付款单明细数据删除
     *
     * @param map map
     */
    void deleteReceiptDetail(Map map);

    /**
     * 付款单数据删除
     *
     * @param map map
     */
    void deleteReceipt(Map map);

    /**
     * 根据付款单删除明细数据
     *
     * @param map map
     */
    void deleteReceiptDetailAll(Map map);

    /**
     * 获取文件数据
     *
     * @param id id
     * @return list
     */
    List<Map<String,String>> getFileLists(@Param("id") String id);

    /**
     * 删除上传文件数据
     *
     * @param id id
     * @return int
     */
    int delFile(String id);

    /**
     * 查询某人的人工复合风险
     *
     * @param idNumber 身份证账号
     * @return int
     */
    Map<String,Object> getRiskInfo(@Param("idNumber") String idNumber);

    /**
     * 付款单付款通过
     *
     * @param list list
     * @return Integer
     */
    Integer updateActualPaymentStatus(@Param("list")List<Map<String,Object>> list);

    Integer getCmreceipt(Map map);


    Map<String,Object> getVlinkDataByReceiptId(@Param("id")String id);

    List<Map<String,Object>> getVlinkListDataByReceiptId(@Param("id") String id);

    List<Map<String,Object>> getVlinkListByReceiptId(@Param("id") String id);

    Integer updateVlinkProjectId(@Param("id") String id,@Param("projectId") String projectId);

    Integer updateVlinkPaymentStatus(Map<String,String> map);

    /**
     * 付款单查询
     *
     * @param id id
     * @return int
     */
    Map<String,Object> selectReceiptById(@Param("id")String id);

    /**
     * 查询当前登录人所在部门
     *
     * @param id id
     * @return String String
     * */
    Map<String,Object> getReceiptInvoiceByJsonId(@Param("id")String id);

    /**
     * 添加审批审请
     *
     * @param map map
     * @return row
     * */
    int initReceiptApply(Map<String,Object> map);

    /**
     * 修改审批审请
     *
     * @param map map
     * @return row
     * */
    int updateReceiptApply(Map<String,Object> map);


    /**
     * 查询单个审请条数
     *
     * @param id id
     * @return list
     * */
    String selectFlowInfoById(@Param("id")String id);

    /**
     * 查询单个审请条数
     *
     * @param id id
     * @return list
     * */
    String selectReceiptApplyById(@Param("id")String id);


}
