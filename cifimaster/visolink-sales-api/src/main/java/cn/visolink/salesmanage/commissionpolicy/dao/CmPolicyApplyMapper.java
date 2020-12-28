package cn.visolink.salesmanage.commissionpolicy.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CmPolicyApplyMapper {

    /**
     * 获取佣金政策
     * @param map 参数
     * @return 政策列表
     * */
    List<Map> getPolicy (Map map);

    /**
     * 导出政策明细
     * @param map 参数
     * @return 政策列表
     * */
    List<Map> exportPolicy (Map map);
    /**
     * 获取佣金政策明细
     * @param policyId 政策ID
     * @return 政策详情
     * */
    Map getBrokerPolicyDetail (String policyId);
    /**
     * 获取佣金政策组织集合
     * @param bizId 政策ID
     * @return 政策详情
     * */
    List<Map> getBizOrgList (String bizId);
    /**
     * 获取佣金政策明细
     * @param policyId 政策ID
     * @return 政策详情
     * */
    Map getAgencyPolicyDetail (String policyId);

    /**
     * 获取政策附件
     * @param policyId 政策ID
     * @return 附件
     * */
    List<Map> getPolicyFile (String policyId);



    /**
     * 获取流程编号
     * @param policyId 政策ID
     * @return 流程编号
     * */
    String getFlowId (String policyId);

    /*
    * 获取分期
    * @param policyId 政策ID
    * @return 附件
    * */
    List<Map> getProjectStage(String projectId);

    /**
     * 修改政策状态
     * @param policyId 政策ID
     * @param state 状态
     * @return 结果
     * */
    int updatePolicyStatus (@Param("policyId") String policyId,@Param("state") String state);

    /**
     * 删除政策
     * @param policyId 政策ID
     * @return 结果
     * */
    int deletePolicy (String policyId);

    /**
     * 删除政策
     * @param policyId 政策ID
     * @return 结果
     * */
    int deletePolicyDetail (String policyId);

    /**
     * 删除政策
     * @param policyId 政策ID
     * @return 结果
     * */
    int deleteFile (String policyId);

    /**
     * 删除政策
     * @param policyId 政策ID
     * @return 结果
     * */
    int deleteFlow (String policyId);


    /**
    * 保存政策主表信息
     * @param map 参数
     * @return 结果
    * */
    int saveCmPolicy(Map map);

    /**
     * 保存经纪人政策表信息
     * @param map 参数
     * @return 结果
     * */
    int saveBrokerPolicy(Map map);

    /**
     * 保存中介政策表信息
     * @param map 参数
     * @return 结果
     * */
    int saveAgencyPolicy(Map map);

    /**
     * 保存经纪人政策表信息到流程表
     * @param map 参数
     * @return 结果
     * */
    int saveBrokerPolicyFlow(Map map);

    /**
     * 保存组织集合到中间表
     * @param bizId 业务数据ID
     * @param list 组织集合
     * @return 结果
     * */
    int saveOrgList(@Param("bizId") String bizId,@Param("list") List list);

    /**
     * 清除组织中间表
     * @param bizId 业务数据ID
     * @return 结果
     * */
    int delOrgList(@Param("bizId") String bizId);
    /**
     * 更新附件表
     * @param map 参数
     * @return 结果
     * */
    int updateFileBizId(@Param("fileId") String fileId, @Param("policyId") String policyId);

    int updateFileStatus(@Param("policyId") String policyId);

    /**
     * 更新政策表
     * @param map 参数
     * @return 结果
     * */
    int updatePolicy(Map map);

    /**
     * 更新经纪人政策表
     * @param map 参数
     * @return 结果
     * */
    int updateBrokerPolicy(Map map);

    /**
     * 更新中介政策表
     * @param map 参数
     * @return 结果
     * */
    int updateAgencyrPolicy(Map map);

    /**
     * 更新流程审批
     * @param map 参数
     * @return 结果
     * */
    int updateFlow(Map map);


    String getProjectByPolicyID(String policyId);

    String getFlowByPolicyID(String policyId);

    String getDateByPolicyID(String policyId);

    /**
     * 更新结束时间今天之前的政策禁用
     * @param date 参数
     * @return 保存结果
     * */
    int updatePolicyStatusByDate(String date);


}
