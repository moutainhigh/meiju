package cn.visolink.system.job.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="CommonJobs对象", description="出参")
public class CommonJobs implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @TableField("JobCode")
    private String jobCode;

    @TableField("JobName")
    private String jobName;

    @ApiModelProperty(value = "JobDesc")
    @TableField("JobDesc")
    private String jobDesc;

    @TableField("AuthCompanyID")
    private String authCompanyId;

    @TableField("ProductID")
    private String productId;

    @TableField("Creator")
    private String creator;

    @TableField("CreateTime")
    private Date createTime;

    @TableField("Editor")
    private String editor;

    @TableField("EditTime")
    private Date editTime;

    @TableField("Status")
    private Boolean status;

    @TableField("IsDel")
    private Boolean isDel;

    @TableField("isIdm")
    private Boolean isIdm;
}
