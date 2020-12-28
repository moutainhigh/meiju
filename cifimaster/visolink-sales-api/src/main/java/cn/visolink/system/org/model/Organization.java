package cn.visolink.system.org.model;

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
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_organization")
@ApiModel(value="Organization对象", description="")
public class Organization implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "父Id")
    @TableField("PID")
    private String pid;

    @ApiModelProperty(value = "父级组织名称")
    private String pName;

    @ApiModelProperty(value = "组织机构代码")
    @TableField("OrgCode")
    private String orgCode;

    @ApiModelProperty(value = "组织机构名称")
    @TableField("OrgName")
    private String orgName;

    @ApiModelProperty(value = "简称")
    @TableField("OrgShortName")
    private String orgShortName;

    @ApiModelProperty(value = "分类：1. 集团2项目3渠道 4案场5自渠6外渠")
    @TableField("OrgCategory")
    private int orgCategory;

    @ApiModelProperty(value = "排序")
    @TableField("ListIndex")
    private Integer listIndex;

    @ApiModelProperty(value = "层级")
    @TableField("Levels")
    private Integer levels;

    @ApiModelProperty(value = "全路径")
    @TableField("FullPath")
    private String fullPath;

    @ApiModelProperty(value = "认证公司Id")
    @TableField("AuthCompanyID")
    private String authCompanyID;

    @ApiModelProperty(value = "产品Id")
    @TableField("ProductID")
    private String productId;

    @ApiModelProperty(value = "创建人")
    @TableField("Creator")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    @TableField("CreateTime")
    private Date createTime;

    @ApiModelProperty(value = "修改人")
    @TableField("Editor")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    @TableField("EditTime")
    private Date editTime;

    @ApiModelProperty(value = "状态")
    @TableField("Status")
    private Integer status;

    @ApiModelProperty(value = "是否删除")
    @TableField("IsDel")
    private Boolean isDel;

    @ApiModelProperty(value = "当前节点")
    @TableField("CurrentPoint")
    private BigDecimal currentPoint;

    @ApiModelProperty(value = "项目ID ")
    @TableField("ProjectID")
    private String projectId;

    @ApiModelProperty(value = "组织公司ID  (案场-代理 、拓客-分销创建组织时，需要选择公司) ")
    @TableField("OrgCompanyID")
    private String orgCompanyId;

    @ApiModelProperty(value = "1--自渠  2 --外渠  3-- 案场")
    @TableField("OrgType")
    private Integer orgType;


    private String FullNo;


}
