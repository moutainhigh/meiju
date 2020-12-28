package cn.visolink.salesmanage.onlineretailersuse.service;

import cn.visolink.exception.ResultBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2020/11/26 9:02 下午
 */
public interface OnlineretailersUseService {
    /**
     * 电商使用政策申请列表查询
     */
    public ResultBody getOnlineretailersUseApplayList(Map paramMap);

    /**
     * 暂存/提交审批
     */
    public ResultBody saveOnlineretailersUseApplay(Map paramMap, HttpServletRequest request);

    /**
     * 电商使用政策申请数据详情查询
     */
    public ResultBody queryOnOnlineretailersUseInfo(Map paramMap, HttpServletRequest request);

    /**
     * 电商使用政策申请同步明源
     */
    public ResultBody synOnOnlineretailersUseData(Map<String, String> paramMap);

    /**
     * 获取对应的产品
     */
    public ResultBody getProductData(Map<String, String> paramMap);

    /**
     * 删除电商政策申请业务数据
     */
    public ResultBody deleteOnOnlineretailersUseData(Map<String, String> paramMap);

}
