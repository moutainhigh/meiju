package cn.visolink.salesmanage.flow.service;

import cn.visolink.common.bean.VisolinkResultBody;
import cn.visolink.exception.ResultBody;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface FlowService {

    //发起流程
    //Map<String, Object> params,,String instanceId
    ResultBody startFlowNew(Map map, String BSID, String BTID, String bkUserID, String BOID, String LoginKey);

    //路径接口
    Map selectUrl(String BOID);

    //定时任务--对比周预估数据
    String weeklyForecast(String params);

    /**
     *
     * 校验是否是关联交易*/
    ResultBody transaction(Map map);

    /**
     * oa回调，手动调用
     */
    void tOAcallback(Map map);

}
