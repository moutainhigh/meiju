package cn.visolink.salesmanage.gxcinterface.service;

import cn.visolink.exception.ResultBody;

import java.util.List;
import java.util.Map;

public
interface GXCInterfaceservice {

    /**
     * 动态货值视图数据增量插入
     */
    ResultBody insertDynamicValue(Map params);

    ResultBody insertPlanValue(Map params);

    ResultBody insertSignPlan(Map params);

    ResultBody insertSupplyPlan(Map params);

    ResultBody insertReportValue(Map params);
    /**
     * 供货视图数据插入
     */
    ResultBody insertvaluegh();

    /**
     * 动态货值视图数据插入
     */
    ResultBody insertvaluedthz();

    /**
     * 战规货值视图数据插入
     */
    ResultBody insertvaluezghz();

    /**
     * 签约视图数据插入
     */
    ResultBody insertvalueqy();

    /**
     * 明源与供销存面积段归集，修改库存可售，正常非车位的
     */
     int updateAvailableStock();

    /**
     * 明源与供销存面积段归集，修改库存可售，车位的
     */
    int updateAvailableStockNotCar();


}
