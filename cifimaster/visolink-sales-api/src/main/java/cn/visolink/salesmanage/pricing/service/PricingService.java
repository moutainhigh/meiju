package cn.visolink.salesmanage.pricing.service;

import cn.visolink.exception.ResultBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author sjl
 * @Created date 2019/11/11 3:59 下午
 */

public interface PricingService {

    /**
     * 解析明源数据
     */
    public Map analysisMyData(Map map);

    /**
     * 渲染本系统定调价编制页面数
     */
    public ResultBody getApplyData(Map map);

    /**
     * 获取定调价字典数据
     */
    ResultBody getPricingDictoryList(Map map);

    //定调价编制
    ResultBody pricingAuthorizedStrength(Map<String, Object> map);

    //上传附件
    ResultBody uploadAttachment(MultipartFile multipartFile);

    ResultBody deleteFile(Map map);

    /**
     * 获取定调价-经营对标-货值数据
     *
     * @param map map
     * @return return
     */
    ResultBody getBusinessIndicatorsValue(Map map);

    /**
     * 获取定调价-价格对比数据
     *
     * @param map map
     * @return return
     */
    ResultBody getPriceComparisonData(Map map);

    /**
     * 获取定调价-定调价结果
     *
     * @param map map
     * @return return
     */
    ResultBody getPriceAdjustmentResult(Map map);

    /**
     * 获取定调价-规划货值压力测算
     *
     * @param map map
     * @return return
     */
    ResultBody getPressureMeasurement(Map map);

    /**
     * 获取定调价-所有统计数据
     *
     * @param map map
     * @return return
     */
    ResultBody getAllStatistics(Map map);

    /**
     * 定调价-价格监测
     *
     * @param map map
     * @return return
     */
    ResultBody priceMonitor(Map map);

    /**
     * 获取规划楼栋调整
     *
     * @param map map
     * @return return
     */
    ResultBody getAdjustmentInfo(Map map);
}
