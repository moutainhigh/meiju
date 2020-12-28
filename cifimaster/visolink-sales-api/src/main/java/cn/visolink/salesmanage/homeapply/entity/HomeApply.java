package cn.visolink.salesmanage.homeapply.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 夏威审批流实体
 * </p>
 *
 * @author bql
 * @since 2020-09-16
 */

@Data
@TableName("mm_home_apply")
public class HomeApply implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    @ApiModelProperty(name = "id", value = "主键")
    private String id;


    @ApiModelProperty(name = "applyName", value = "主数据项目id")
    @TableField("apply_name")
    private String applyName;


    @ApiModelProperty(name = "applyTime", value = "主数据项目id")
    @TableField("apply_time")
    private String applyTime;

    @ApiModelProperty(name = "applyRegion", value = "主数据项目id")
    @TableField("apply_region")
    private String applyRegion;

    @ApiModelProperty(name = "applyDepartment", value = "主数据项目id")
    @TableField("apply_department")
    private String applyDepartment;

    @ApiModelProperty(name = "applyTheme", value = "主数据项目id")
    @TableField("apply_theme")
    private String applyTheme;

    @ApiModelProperty(name = "applyType", value = "主数据项目id")
    @TableField("apply_type")
    private String applyType;

    @ApiModelProperty(name = "applySystem", value = "主数据项目id")
    @TableField("apply_system")
    private String applySystem;

    @ApiModelProperty(name = "applyText", value = "主数据项目id")
    @TableField("apply_text")
    private String applyText;

    @ApiModelProperty(name = "flowCode", value = "主数据项目id")
    @TableField("flow_code")
    private String flowCode;

    @ApiModelProperty(name = "isDel", value = "主数据项目id")
    @TableField("isdel")
    private int isDel;

    @ApiModelProperty(name = "createTime", value = "主数据项目id")
    @TableField("create_time")
    private String createTime;

    @ApiModelProperty(name = "creator", value = "主数据项目id")
    @TableField("creator")
    private String creator;

    @ApiModelProperty(name = "editTime", value = "主数据项目id")
    @TableField("edit_time")
    private String editTime;

    @ApiModelProperty(name = "editor", value = "主数据项目id")
    @TableField("editor")
    private String editor;

    @ApiModelProperty(name = "jobOrgId", value = "主数据项目id")
    @TableField("job_org_id")
    private String jobOrgId;

    @ApiModelProperty(name = "jobId", value = "主数据项目id")
    @TableField("job_id")
    private String jobId;

    @ApiModelProperty(name = "orgId", value = "主数据项目id")
    @TableField("org_id")
    private String orgId;

}
