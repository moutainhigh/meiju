package cn.visolink.system.company.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * CompanyInfoVO对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
@Data
@ApiModel(value="CompanyInfo对象", description="")
public class CompanyInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

        private String id;

        private Integer rownum;

        @ApiModelProperty(value = "公司代码")
                private String companyCode;

        @ApiModelProperty(value = "公司全称")
                private String companyName;

        @ApiModelProperty(value = "简称")
                private String shortName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                private Date createTime;

        private String creator;

         @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date edittime;

        private String editor;

        @ApiModelProperty(value = "备注")
        private String note;

        @ApiModelProperty(value = "显示排序")
                private Integer showSort;

        @ApiModelProperty(value = "公司属性:1--自渠  2 --外渠  3-- 案场")
                private Integer companyAttr;

        @ApiModelProperty(value = "是否启用")
                private Integer Status;

        @ApiModelProperty(value = "是否删除")
                private Integer IsDel;

        @ApiModelProperty(value = "开始时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                private Date startTime;

        @ApiModelProperty(value = "结束时间")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                private Date endTime;


}
