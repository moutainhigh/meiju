package cn.visolink.salesmanage.checklist.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 佣金核算单明细
 * </p>
 *
 * @author yangjie
 * @since 2020-05-17
 */
@Data
@TableName("cm_checklist_detail")
public class ChecklistDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(name = "id", value = "主键")
    private String id;

    @ApiModelProperty(name = "fid", value = "父核算单id")
    private String fid;

    @ApiModelProperty(name = "checklistId", value = "核算单id")
    @TableField("checklist_id")
    private String checklistId;

    @ApiModelProperty(name = "commissionId", value = "待结佣id")
    @TableField("commission_id")
    private String commissionId;

    @ApiModelProperty(name = "roomId", value = "房间id")
    @TableField("room_id")
    private String roomId;

    @ApiModelProperty(name = "businessAttributionCode", value = "业务归属人编号")
    @TableField("business_attribution_code")
    private String businessAttributionCode;

    @ApiModelProperty(name = "transactionId", value = "交易id")
    @TableField("transaction_id")
    private String transactionId;

    @ApiModelProperty(name = "opportunityId", value = "机会id")
    @TableField("opportunity_id")
    private String opportunityId;

    @ApiModelProperty(name = "projectCode", value = "立项编号")
    @TableField("project_code")
    private String projectCode;

    @ApiModelProperty(name = "projectTime", value = "立项时间")
    @TableField("project_time")
    private String projectTime;

    @ApiModelProperty(name = "projectAmount", value = "立项金额")
    @TableField("project_amount")
    private BigDecimal projectAmount;

    @ApiModelProperty(name = "commissionPoint", value = "佣金点位")
    @TableField("commission_point")
    private BigDecimal commissionPoint;

    @ApiModelProperty(name = "amountClosed", value = "已结金额")
    @TableField("amount_closed")
    private BigDecimal amountClosed;

    @ApiModelProperty(name = "outstandingAmount", value = "未结金额")
    @TableField("outstanding_amount")
    private BigDecimal outstandingAmount;

    @ApiModelProperty(name = "applicationAmount", value = "申请金额")
    @TableField("application_amount")
    private BigDecimal applicationAmount;

    @ApiModelProperty(name = "applicant", value = "申请人")
    private String applicant;

    @ApiModelProperty(name = "amountPaid", value = "已付金额")
    @TableField("amount_paid")
    private BigDecimal amountPaid;

    @ApiModelProperty(name = "unpaidAmount", value = "未付金额")
    @TableField("unpaid_amount")
    private BigDecimal unpaidAmount;

    @ApiModelProperty(name = "comPaymentRatio", value = "佣金付款比列")
    @TableField("com_payment_ratio")
    private BigDecimal comPaymentRatio;

    @ApiModelProperty(name = "isDeadlock", value = "是否锁死：0 否、1 是")
    @TableField("is_deadlock")
    private Integer isDeadlock;

    @ApiModelProperty(name = "isHide", value = "是否隐藏：0 否、1 是")
    @TableField("is_hide")
    private Integer isHide;

    @ApiModelProperty(name = "checklistDetailType", value = "核算单明细类型：1 正核算单明细、2 负核算单明细、3 欠款、4 欠款抵扣")
    @TableField("checklist_detail_type")
    private Integer checklistDetailType;

    @ApiModelProperty(name = "isSettle", value = "欠款是否结清：0 未结清、1 已结清")
    @TableField("is_settle")
    private Integer isSettle;

    @ApiModelProperty(name = "isNegative", value = "是否已关联负核算单：0  未关联负核算单，1 已关联负核算单")
    @TableField("is_negative")
    private Integer isNegative;

    @ApiModelProperty(name = "status", value = "状态：0 禁用、1 启用", hidden = true)
    private Integer status;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(name = "isdel", value = "是否删除：0 否、1 是", hidden = true)
    private Integer isdel;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(name = "createTime", value = "创建时间", hidden = true)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(name = "creator", value = "创建人")
    private String creator;

    @ApiModelProperty(name = "jobOrgId", value = "直属部门ID")
    @TableField(value = "job_org_id")
    private String jobOrgId;

    @ApiModelProperty(name = "jobId", value = "岗位ID")
    @TableField(value = "job_id")
    private String jobId;

    @ApiModelProperty(name = "orgId", value = "层级组织ID")
    @TableField(value = "org_id")
    private String orgId;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(name = "editTime", value = "修改时间", hidden = true)
    @TableField(value = "edit_time", fill = FieldFill.INSERT_UPDATE)
    private Date editTime;

    @ApiModelProperty(name = "editor", value = "修改人")
    private String editor;
}
