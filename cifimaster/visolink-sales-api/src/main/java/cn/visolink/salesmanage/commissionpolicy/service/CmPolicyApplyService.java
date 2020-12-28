package cn.visolink.salesmanage.commissionpolicy.service;

import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface CmPolicyApplyService {


    /**
     * 获取佣金政策
     * @param map 参数
     * @return 政策列表
     * */
    PageInfo getPolicy (Map map);

    /**
     * 获取佣金政策明细
     * @param policyId 政策ID
     * @return 政策详情
     * */
    Map getPolicyDetail (String policyId,String policyType);


    List<Map> getProjectStage(String projectId);

    /**
     * 删除佣金政策明细
     * @param policyId 政策ID
     * @return 结果
     * */
    Map deletePolicy (String policyId);

    /**
     * 修改政策状态
     * @param policyId 政策ID
     * @param state 状态
     * @return 结果
     * */
    Map updatePolicyStatus (String policyId,String state);


    /**
     * 全民营销政策申请
     * @param map 参数
     * @return 保存结果
     * */
    int brokerPolicyApply (Map map);


    /**
     * 中介政策申请
     * @param map 参数
     * @return 保存结果
     * */
    int agencyPolicyApply (Map map);

    /**
     * 政策修改
     * @param map 参数
     * @return 保存结果
     * */
    int updatePolic (Map map);




}
