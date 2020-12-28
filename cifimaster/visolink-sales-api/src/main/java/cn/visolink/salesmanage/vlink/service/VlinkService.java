package cn.visolink.salesmanage.vlink.service;

import cn.visolink.utils.BaseResponse;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author baoql
 * @since 2020-09-01
 */

public interface VlinkService {

    /**
     * Post
     *
     * @param data date
     * @param funCode 接口参数
     * @return BaseResponse
     * */
    BaseResponse doPost(Object data, String funCode);

    /**
     *立项通过后回调薇链
     *
     * @param request request
     * @param receiptId 接口参数
     * @return BaseResponse
     * */
    void vlinkProjectApprove(HttpServletRequest request, String receiptId);

    /**
     *申请项目通过回调薇链
     *
     * @param receiptId 接口参数
     * @return BaseResponse
     * */
    void vlinkBatchPayment(String receiptId);

    /**
     *申请项目通过后回调接口
     *
     * @param date date
     * */
    void approvalVlinkDate(String date);

    /**
     *付款通过后回调接口
     *
     * @param date date
     * */
    void paymentVlinkDate(String date);


    void setVlinkLogs(String taskName,String note);
}
