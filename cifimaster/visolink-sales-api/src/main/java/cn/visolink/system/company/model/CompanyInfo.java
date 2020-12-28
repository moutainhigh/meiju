package cn.visolink.system.company.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2019-08-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_company_info")
@ApiModel(value="CompanyInfo对象", description="")
public class CompanyInfo implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "公司代码")
    @TableField("companyCode")
    private String companyCode;

    @ApiModelProperty(value = "公司全称")
    @TableField("companyName")
    private String companyName;

    @ApiModelProperty(value = "简称")
    @TableField("shortName")
    private String shortName;

    @TableField("createTime")
    private Date createTime;

    private String creator;

    private Date edittime;

    private String editor;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "显示排序")
    @TableField("showSort")
    private Integer showSort;

    @ApiModelProperty(value = "公司属性:1--自渠  2 --外渠  3-- 案场")
    @TableField("companyAttr")
    private Integer companyAttr;

    @ApiModelProperty(value = "是否启用")
    @TableField("Status")
    private Integer Status;

    @ApiModelProperty(value = "是否删除")
    @TableField("IsDel")
    private Integer IsDel;

    @ApiModelProperty(value = "开始时间")
    @TableField("startTime")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("endTime")
    private Date endTime;


}
