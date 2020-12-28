
package cn.visolink.system.company.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * CompanyInfoForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "CompanyInfo对象", description = "")
public class CompanyInfoForm extends Page {

    private static final long serialVersionUID = 1L;

    private String id;

    private Integer pageSize;

    private Integer pageNum;

    @ApiModelProperty(value = "公司代码")
    private String companyCode;

    @ApiModelProperty(value = "公司全称")
    private String companyName;

    @ApiModelProperty(value = "简称")
    private String shortName;

    private String createTime;

    private String creator;

    private String editTime;

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
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;


}
