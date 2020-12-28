package cn.visolink.salesmanage.caopandata.service;

import cn.visolink.exception.ResultBody;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/9 11:06 上午
 */
public interface CaoPanDataService {

    /**
     * 签约信息
     */
    ResultBody getSigningData();


    /**
     * nos增量签约信息
     */
    ResultBody getNosSigningAdd(Map params);



    ResultBody initCostData();


}
