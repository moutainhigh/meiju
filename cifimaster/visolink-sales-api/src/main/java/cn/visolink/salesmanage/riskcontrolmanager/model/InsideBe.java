package cn.visolink.salesmanage.riskcontrolmanager.model;

import lombok.Data;

@Data
public class InsideBe {

    private String   businessUnit;
    private String  projectId;
    private String  project_name;
    private String  client_id;
    private String  client_name;

    private String  freshcard_time;

    private String  create_time;
    private String   firstphoto_time;
    private String  id_number;
    private String  system_risk;
    private String  risk_approve_status;
    private String  risk_approve_remark;
    private String   risk_approve_time;
    private String   reject_time;
    private String  reject_content;
    private String  remark;
    private String   report_time;
    private String   import_time;
    private String  agent;
    private String counselor_name;
    private String   channel;
    private String  state;
    private String   risk_time;
    private String  opening_time;
    private String  risk_reason;
    private String   riskSpan;

    private String  roominfo;

    private String  projectCode;
    private String  mainMediaName;
    private String  orderGuid;
    private String   ywgsDate;
    private String  closeReason;

    public Object[] toExportExcelData(){
        return new Object[]{
                //集团/事业部名称
                getBusinessUnit(),
                //项目名称
                getProject_name(),
                //项目编码
                getProjectCode(),
                //房间信息
                getRoominfo(),
                //业务归属时间
                getYwgsDate(),
                //状态
                getCloseReason(),
                //置业顾问名称
                getCounselor_name(),
                //成交渠道,
                getChannel(),
                //渠道姓名
                getAgent(),
                //客户姓名
                getClient_name(),
                //身份证号
                getId_number(),
                //刷证时间
                getFreshcard_time(),
                //报备时间
                getReport_time(),
                //首次抓拍时间
                getFirstphoto_time(),
                //导入时间
                getImport_time(),
                //系统风险提示时间
                getRisk_time(),
                //系统风险提示原因
                getRisk_reason(),
                //系统风险状态
                getSystem_risk(),
                //人工复合状态
                getRisk_approve_status(),
                //人工复核时间
                getRisk_approve_time(),
                //人工复核处理时长，
                getRiskSpan(),
                //人工复核原因
                getRisk_approve_remark(),
                //驳回复核时间
                getReject_time(),
                //驳回原因
                getReject_content(),
                //备注
                getRemark()
        };
    }

}
