package cn.visolink.salesmanage.gxcinterface.service;

import cn.visolink.exception.ResultBody;

import java.text.ParseException;
import java.util.Map;

/**
 * Description:
 *
 * @author: 杨航行
 * @date: 2020.04.08
 */
public interface NewGxcByProjectService {



    /**
     * 动态货值视图数据增量插入
     */
    ResultBody insertDynamicValue(Map params);


    ResultBody insertSignPlan(Map params);

    ResultBody insertSupplyPlan(Map params);

    public void initmmidm(String projectId);

    void updataBusinsee(String projectId);

    ResultBody intiSingDataAll(String projectId);

    /**
     * 获取事业部数据
     * @param map
     * @return
     */
    int getBusiness(Map map);

    ResultBody insertReportValue(Map params);
    ResultBody insertReportValueAll();

    /**
    * 初始化所有供货
     *
    * */
    ResultBody getSupplyValue() throws ParseException;

}
