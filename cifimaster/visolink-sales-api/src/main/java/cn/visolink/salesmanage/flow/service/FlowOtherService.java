package cn.visolink.salesmanage.flow.service;

import cn.visolink.exception.ResultBody;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 废弃流程
 */
public interface FlowOtherService {


    /**
     * 废弃流程
     */
    ResultBody endFlow(String instanceId);

    /**
     * 删除流程
     */
    ResultBody deleteFlow(String instanceId);

    /**
     * 查询流程id接口
     * */
    Map queryFlowId(@Param("BOID") String BOID);


}
