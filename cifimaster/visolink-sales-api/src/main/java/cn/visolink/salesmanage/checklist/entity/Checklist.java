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
 * 佣金核算单
 * </p>
 *
 * @author yangjie
 * @since 2020-05-14
 */
@Data
@TableName("cm_checklist")
public class Checklist implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(name = "id", value = "主键")
    private String id;

    @ApiModelProperty(name = "fid", value = "父核算单id")
    private String fid;

    @ApiModelProperty(name = "fcode", value = "父核算单编号")
    private String fcode;

    @ApiModelProperty(name = "mainDataProjectId", value = "主数据项目id")
    @TableField("main_data_project_id")
    private String mainDataProjectId;

    @ApiModelProperty(name = "checklistName", value = "核算单名称")
    @TableField("checklist_name")
    private String checklistName;

    @ApiModelProperty(name = "checklistCode", value = "核算单编号")
    @TableField("checklist_code")
    private String checklistCode;

    @ApiModelProperty(name = "dealType", value = "成交类型")
    @TableField("deal_type")
    private String dealType;

    @ApiModelProperty(name = "channelName", value = "渠道名称")
    @TableField("channel_name")
    private String channelName;

    @ApiModelProperty(name = "businessAttributionCode", value = "业务归属人编号")
    @TableField("business_attribution_code")
    private String businessAttributionCode;

    @ApiModelProperty(name = "projectCode", value = "立项编号")
    @TableField("project_code")
    private String projectCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(name = "projectTime", value = "立项时间")
    @TableField("project_time")
    private Date projectTime;

    @ApiModelProperty(name = "projectStatus", value = "立项状态：1 草稿、2 已审核、3 已立项、4 立项通过、5 立项驳回")
    @TableField("project_status")
    private Integer projectStatus;

    @ApiModelProperty(name = "projectAmount", value = "立项金额")
    @TableField("project_amount")
    private BigDecimal projectAmount;

    @ApiModelProperty(name = "paymentClosed", value = "已结付款")
    @TableField("payment_closed")
    private BigDecimal paymentClosed;

    @ApiModelProperty(name = "auditor", value = "审核人")
    private String auditor;

    @ApiModelProperty(name = "isFather", value = "是否父核算单：0 否、1 是")
    @TableField("is_father")
    private Integer isFather;

    @ApiModelProperty(name = "division", value = "事业部")
    private String division;

    @ApiModelProperty(name = "cityCompany", value = "城市公司")
    @TableField("city_company")
    private String cityCompany;

    @ApiModelProperty(name = "projectName", value = "项目名称")
    @TableField("project_name")
    private String projectName;

    @ApiModelProperty(name = "creatorName", value = "创建人名称")
    @TableField("creator_name")
    private String creatorName;

    @ApiModelProperty(name = "isAbnormal", value = "异常标识：0 正常、1 异常")
    @TableField("is_abnormal")
    private Integer isAbnormal;

    @ApiModelProperty(name = "commissionType", value = "结佣形式")
    @TableField("commission_type")
    private String commissionType;

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
