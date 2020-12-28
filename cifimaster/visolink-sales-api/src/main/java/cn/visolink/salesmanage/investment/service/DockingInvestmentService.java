package cn.visolink.salesmanage.investment.service;

import cn.visolink.exception.ResultBody;

import java.util.Map;

/**
 * @author yangjie
 * @date 2020-10-26
 */
public interface DockingInvestmentService {

    /**
     * 获取上会版、拿地后数据（投资系统）
     *
     * @param map map
     * @return return
     */
    ResultBody getInvestmentSystemData(Map<String, Object> map);
}
