package cn.visolink.utils.flowpojo;

import lombok.Data;

import java.util.Map;

@Data
public class SaveFlowRequest {
    /**  业务系统标识（必填） **/
    private String sysCode;

    /**  业务系统流程数据主键（必填） **/
    private String businessKey;

    /**  发起人的账号（必填） **/
    private String account;

    /**  发起人的岗位编码（兼岗时必填） **/
    private String postCode;

    /**  发起人所属部门编码 **/
    private String orgCode;

    /** 流程 key（流程模板标识） */
    private String flowKey;

    /** 流程实例 ID（重新提交时必填）*/
    private String instanceId;

    /**  流程实例标题 **/
    private String subject;

    /**  驳回发起人后是否强制重走 **/
    private boolean backNormal;

    /**  代理发起时必填，否则不填 **/
    private String agentAccount;


    /** 项目id,对应流程档位 **/
    private String startOrgCode;

    /**  流程变量 **/
    private Map<String,String> vars;
    /**
     * 事业部名称
     */
    private String orgName;
}
