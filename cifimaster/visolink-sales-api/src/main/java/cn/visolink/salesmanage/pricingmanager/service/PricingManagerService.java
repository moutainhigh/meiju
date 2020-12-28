package cn.visolink.salesmanage.pricingmanager.service;

import java.util.List;
import java.util.Map;

public interface PricingManagerService {

    /**
     * 跟新审批单
     * @param map
     * @return
     */
    public Integer  updatePricing(Map<String,Object> map);

    /**
     * 编写审批单
     * @param map
     * @return
     */
    public Integer  insertPricing(Map<String,Object> map);


    /**
     * 查找审批单
     * @param map
     * @return
     */
    public List<Map> selectPricing(Map<String,Object> map);
}
