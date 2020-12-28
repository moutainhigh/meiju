package cn.visolink.salesmanage.checklist.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 佣金核算单-政策-中间表
 * </p>
 *
 * @author yangjie
 * @since 2020-05-17
 */
@Data
@TableName("cm_checklist_policy")
public class ChecklistPolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(name = "id", value = "主键")
    private String id;

    @ApiModelProperty(name = "checklistId", value = "核算单id")
    @TableField("checklist_id")
    private String checklistId;

    @ApiModelProperty(name = "policyId", value = "政策id")
    @TableField("policy_id")
    private String policyId;

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
