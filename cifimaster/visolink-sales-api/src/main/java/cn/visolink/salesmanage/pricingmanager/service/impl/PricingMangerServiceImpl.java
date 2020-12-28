package cn.visolink.salesmanage.pricingmanager.service.impl;

import cn.visolink.salesmanage.pricingmanager.dao.PricingManagerDao;
import cn.visolink.salesmanage.pricingmanager.service.PricingManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class PricingMangerServiceImpl implements PricingManagerService {

        @Autowired
    PricingManagerDao pricingManagerDao;
    /**
     * 跟新审批单
     * @param map
     * @return
     */
    @Override
    public Integer updatePricing(Map<String, Object> map) {
        return pricingManagerDao.updatePricing(map);
    }
    /**
     * 编写审批单
     * @param map
     * @return
     */
    @Override
    public Integer insertPricing(Map<String, Object> map) {
        return pricingManagerDao.insertPricing(map);
    }
    /**
     * 查找审批单
     * @param map
     * @return
     */
    @Override
    public List<Map> selectPricing(Map<String, Object> map) {
        return pricingManagerDao.selectPricing(map);
    }
}
