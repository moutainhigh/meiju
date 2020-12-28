package cn.visolink.system.job.common.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * CommonJobsForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
@Data
@EqualsAndHashCode()
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "CommonJobs对象", description = "岗位")
public class CommonJobsForm{

    private static final long serialVersionUID = 1L;

    private String id;

    private String jobCode;

    @ApiModelProperty(value = "岗位名称")
    private String jobName;

    @ApiModelProperty(value = "岗位描述")
    private String jobDesc;

    @ApiModelProperty(value = "公司Id")
    private String authCompanyId;

    @ApiModelProperty(value = "产品Id")
    private String productId;

    private String creator;

    private String createTime;

    private String editor;

    private String editTime;

    private Integer status;

    private Integer isDel;

    private Integer isIdm;

    private Integer current;

    private Integer size;

    private Integer jobType;
}
