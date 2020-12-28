package cn.visolink.utils.flowpojo;

import lombok.Data;

import java.util.List;

/**
 *
 //    "createTime": "2018-12-25 01:26:50", //任务达到时间
 //            "instanceId": "7611067", //流程实例 ID
 //            "taskKey": "SignTask1", //流程环节 Key
 //            "taskName": "相关审批人", //流程环节名称
 //            "auditorName": "张三", //处理人姓名
 //            "postName": "(研发高级经理)", //处理人所属岗位
 //            "orgPath": "集团总部/企业管理中心/信息管理部",//处理人所属组织路径
 //            "opinion": "请领导审批", //处理意见
 //            "status": "agree", //处理动作 key
 //            "statusVal": "通过", //处理动作
 //            "completeTime": "2018-12-25 01:28:07", //处理时间
 //            "durMs": 77103, //处理所用时长(单位：毫秒)
 */
@Data
public class FlowOpinionRes {

    private String createTime;

    private String instanceId;

    private String taskKey;

    private String taskName;

    private String auditorName;

    private String postName;

    private String orgPath;

    private String opinion;

    private String status;

    private String statusVal;

    private String completeTime;

    private String durMs;

    private List<FlowOpinionResFile> files;
}
