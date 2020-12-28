package cn.visolink.salesmanage.signdata.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/18 7:58 下午
 */
public interface SingDataService{

    /**
     * 获取月度+周度签约金额数据
     * @param map
     * @return
     */
    Map getSingMoneyData(Map map);

}
