package cn.visolink.salesmanage.flow.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @author LH
 * @since 2019/11/25
 * @version 1.0
 */
@Mapper
public interface FlowOtherDao {


    /**
     * 废弃审批
     * @param instanceId
     */
    Integer endFlowUpdate(@Param("instance_id") String instanceId);

    /**
     * 查询流程id
     * */
    Map queryFlowId(@Param("BOID") String BOID);
    Map queryFlowIdByBaseid(@Param("BOID") String BOID);

}
