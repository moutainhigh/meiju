package cn.visolink.system.org.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * OrganizationVO对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Data
@ApiModel(value = "Organization对象", description = "")
public class OrganizationVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    @ApiModelProperty(value = "父Id")
    private String pid;

    @ApiModelProperty(value = "组织机构代码")
    private String OrgCode;

    @ApiModelProperty(value = "组织机构名称")
    private String OrgName;

    @ApiModelProperty(value = "简称")
    private String OrgShortName;

    @ApiModelProperty(value = "分类：1. 集团2项目3渠道 4案场5自渠6外渠")
    private Integer OrgCategory;

    @ApiModelProperty(value = "排序")
    private Integer ListIndex;

    @ApiModelProperty(value = "层级")
    private Integer Levels;

    @ApiModelProperty(value = "全路径")
    private String FullPath;

    @ApiModelProperty(value = "认证公司Id")
    private String AuthCompanyID;

    @ApiModelProperty(value = "产品Id")
    private String ProductID;

    @ApiModelProperty(value = "创建人")
    private String Creator;

    @ApiModelProperty(value = "创建时间")
    private Date CreateTime;

    @ApiModelProperty(value = "修改人")
    private String Editor;

    @ApiModelProperty(value = "修改时间")
    private Date EditTime;

    @ApiModelProperty(value = "状态")
    private Integer Status;

    @ApiModelProperty(value = "是否删除")
    private Boolean IsDel;

    @ApiModelProperty(value = "当前节点")
    private BigDecimal CurrentPoint;

    @ApiModelProperty(value = "项目ID ")
    private String ProjectID;

    @ApiModelProperty(value = "组织公司ID  (案场-代理 、拓客-分销创建组织时，需要选择公司) ")
    private String orgCompanyId;

    @ApiModelProperty(value = "1--自渠  2 --外渠  3-- 案场")
    private Integer OrgType;

    @ApiModelProperty(value = "所属组织")
    private String  PName;

    private List<OrganizationVO> children;

    private String FullNo;


}
