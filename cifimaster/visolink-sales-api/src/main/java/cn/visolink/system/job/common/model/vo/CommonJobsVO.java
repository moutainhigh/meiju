package cn.visolink.system.job.common.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * CommonJobsVO对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-29
 */
@Data
@ApiModel(value = "CommonJobs对象", description = "")
public class CommonJobsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String JobCode;

    private String JobName;

    private String JobDesc;

    private String AuthCompanyID;

    private String ProductID;

    private String Creator;

    private Date CreateTime;

    private String Editor;

    private Date EditTime;

    private Boolean Status;

    private Boolean IsDel;

    private Boolean isIdm;

    private int jobNum;

    private int JobType;
}
