package cn.visolink.system.menus.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * MenusVO对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-31
 */
@Data
@ApiModel(value = "Menus对象", description = "")
public class MenusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "父级Id")
    private String pid;

    @ApiModelProperty(value = "菜单名称")
    private String MenuName;

    @ApiModelProperty(value = "菜单Url")
    private String Url;

    @ApiModelProperty(value = "图片Url")
    private String ImageUrl;

    @ApiModelProperty(value = "是否为首页")
    private Integer IsHomePage;

    @ApiModelProperty(value = "菜单是否显示")
    private Integer IsShow;

    @ApiModelProperty(value = "层级")
    private Integer Levels;

    @ApiModelProperty(value = "排序号")
    private Integer ListIndex;

    @ApiModelProperty(value = "全路径")
    private String FullPath;

    @ApiModelProperty(value = "是否末级")
    private Integer IsLast;

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
    private Integer IsDel;

    @ApiModelProperty(value = "菜单系统名称")
    private String MenuSysName;

    private String IconClass;

    @ApiModelProperty(value = "菜单类型 1-管理平台 2-app菜单")
    private Integer menusType;

    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "重定向路径")
    private String redirect;

    @ApiModelProperty(value = "备注")
    private String Remarks;


    private List<MenusVO> children;


}