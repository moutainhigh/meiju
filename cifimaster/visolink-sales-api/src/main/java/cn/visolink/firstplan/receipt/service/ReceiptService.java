package cn.visolink.firstplan.receipt.service;

import cn.visolink.exception.ResultBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 佣金付款单 Service 接口
 * </p>
 *
 * @author baoql
 * @since 2020-05-25
 */


public interface ReceiptService {


    /**
     * 添加付款单
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody initReceipt(Map<String, Object> map);

    /**
     * 添加付款单明细
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody initReceiptDetail(Map<String, Object> map);

    /**
     * 查询付款单
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody selectReceipt(Map<String, Object> map);

    /**
     * 查询待付款
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody selectWaitReceipt(Map<String, Object> map);


    /**
     * 佣金台账查询
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody selectCommissionStanding(Map<String, Object> map);


    /**
     * 查询待付款导出
     *
     * @param map map
     * @return ResultBody
     */
    void selectExcelCommissionStanding(HttpServletRequest request, HttpServletResponse response, String ids, Map<String, Object> map, String type);


    /**
     * 查询待付款导出
     *
     * @param map map
     * @return ResultBody
     */
    void selectExcelWaitReceipt(HttpServletRequest request, HttpServletResponse response, String ids, Map<String, Object> map, String type);

    /**
     * 查询付款单明细
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody selectReceiptDetail(Map<String, Object> map);

    /**
     * 导出付款单明细
     *
     * @param request  request
     * @param response response
     * @param rids     rids
     * @param ids      ids
     */
    void selectExcelReceiptDetail(HttpServletRequest request, HttpServletResponse response, String rids, String ids,Map<String,Object> map, String type);

    /**
     * 导入付款单明细
     *
     * @param file   file
     * @param months months
     * @return ResultBody
     */
    ResultBody receiptDetailImport(String userName,MultipartFile file, String months);

    /**
     * 付款单明细付款金额修改
     *
     * @param list list
     * @param userId userId
     * @return Integer
     */
    Integer updateReceiptDetail(List<Map<String,Object>> list,String userId) ;

    /**
     * 付款单付款金额修改
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody updateReceiptAmount(Map<String,Object> map);

    /**
     * 明源对接数据接口，修改付款单状态
     *
     * @param map map
     * @return ResultBody
     */
    Map receiptAddApproval(HttpServletRequest request,Map map);

    /**
     * 修改付款单状态
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody updatePaymentStatus(Map map);

    /**
     * 付款单生成验证
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody getPaymentStatus(Map map);

    /**
     * 查询当前付款单明细上传文件
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody getFileList(Map<String, Object> map);

    /**
     * 删除当前付款单明细上传文件
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody delFile(Map map);

    /**
     * 删除付款单
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody deleteReceipt(Map map);

    /**
     * 删除付款单明细
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody deleteReceiptDetail(Map map);

    /**
     * 局部新明源数据
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody updateReceiptMyTrade(Map map);

    /**
     * 执行付款单审批数据验证
     *
     * @param map map
     * @return ResultBody
     */
    ResultBody getMyStatus(Map map);

    /**
     * 同步付款单付款数据
     *
     */
    Integer getMYhtfkapply();

    /**
     * 佣金付款单审批流数据
     *
     * @param map map
     * @return Map
     */
    ResultBody selectInvoiceApplication(Map<String,Object> map);

    /**
     * 佣金付款单审批流申请
     *
     * @param map map
     * @return Map
     */
    ResultBody initInvoiceApplication(Map<String, Object> map);

}
