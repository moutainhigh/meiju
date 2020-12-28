package cn.visolink.firstplan.preemptionopen.service;

import cn.visolink.exception.ResultBody;

import java.util.Map;

/**
 * @author sjl 抢开申请
 * @Created date 2020/7/15 11:14 上午
 */
public interface PreemptionOpenService {

    ResultBody viewPreemptionOpenData(Map map);

    /**
     * 获取周拆分数据
     */
    public ResultBody getWeekSpiltData(Map map);
    /**
     * 保存数据
     */
    public ResultBody savePreemptionOpenData(Map map);
    /**
     * 切换版本
     */
    public ResultBody swicthVersion(Map map);
    /**
     * 审批通过回调
     */
    public ResultBody approvedCallback(Map map);
    /**
     * 初始化客储数据
     */
    public void initCustomerStroageData(Map map);
}
