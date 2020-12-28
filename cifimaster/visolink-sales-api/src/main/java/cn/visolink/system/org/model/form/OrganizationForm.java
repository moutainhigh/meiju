package cn.visolink.system.org.model.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * OrganizationForm对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "Organization对象", description = "")
public class OrganizationForm extends Page {

    private static final long serialVersionUID = 1L;

    private String id;

    @ApiModelProperty(value = "父Id")
    private String pid;

    @ApiModelProperty(value = "组织机构代码")
    private String orgCode;

    private String companyName;

    @ApiModelProperty(value = "组织机构名称")
    private String orgName;

    @ApiModelProperty(value = "简称")
    private String orgShortName;

    @ApiModelProperty(value = "分类：1. 集团2项目3渠道 4案场5自渠6外渠")
    private int orgCategory;

    @ApiModelProperty(value = "排序")
    private Integer listIndex;

    @ApiModelProperty(value = "层级")
    private Integer levels;

    @ApiModelProperty(value = "全路径")
    private String fullPath;

    @ApiModelProperty(value = "认证公司Id")
    private String authCompanyId;

    @ApiModelProperty(value = "产品Id")
    private String productId;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "修改人")
    private String editor;

    @ApiModelProperty(value = "修改时间")
    private String editTime;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "是否删除")
    private Boolean isDel=false;

    @ApiModelProperty(value = "当前节点")
    private BigDecimal currentPoint;

    @ApiModelProperty(value = "项目ID ")
    private String projectId;

    @ApiModelProperty(value = "组织公司ID  (案场-代理 、拓客-分销创建组织时，需要选择公司) ")
    private String orgCompanyId;

    @ApiModelProperty(value = "1--自渠  2 --外渠  3-- 案场")
    private Integer orgType;

    private String userName;
    @ApiModelProperty(value = "是否需要展示已经禁用的")
    private String isNeedShow;


    private String orgLevel;
    private String orgLevelName;
    private String parentLevelID;
    private String parentLevelName;

    private String FullNo;


}
