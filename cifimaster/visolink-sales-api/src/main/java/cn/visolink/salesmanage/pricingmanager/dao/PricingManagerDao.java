package cn.visolink.salesmanage.pricingmanager.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface PricingManagerDao {
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
