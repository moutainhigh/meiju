package cn.visolink.system.menus.model;

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
@TableName("s_menus")
@ApiModel(value = "Menus对象", description = "")
public class Menus implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "父级Id")
    @TableField("PID")
    private String pid;

    @ApiModelProperty(value = "菜单名称")
    @TableField("MenuName")
    private String MenuName;

    @ApiModelProperty(value = "菜单Url")
    @TableField("Url")
    private String Url;

    @ApiModelProperty(value = "图片Url")
    @TableField("ImageUrl")
    private String ImageUrl;

    @ApiModelProperty(value = "是否为首页")
    @TableField("IsHomePage")
    private Integer IsHomePage;

    @ApiModelProperty(value = "菜单是否显示")
    @TableField("IsShow")
    private Integer IsShow;

    @ApiModelProperty(value = "层级")
    @TableField("Levels")
    private Integer Levels;

    @ApiModelProperty(value = "排序号")
    @TableField("ListIndex")
    private Integer ListIndex;

    @ApiModelProperty(value = "全路径")
    @TableField("FullPath")
    private String FullPath;

    @ApiModelProperty(value = "是否末级")
    @TableField("IsLast")
    private Integer IsLast;

    @ApiModelProperty(value = "创建人")
    @TableField("Creator")
    private String Creator;

    @ApiModelProperty(value = "创建时间")
    @TableField("CreateTime")
    private Date CreateTime;

    @ApiModelProperty(value = "修改人")
    @TableField("Editor")
    private String Editor;

    @ApiModelProperty(value = "修改时间")
    @TableField("EditTime")
    private Date EditTime;

    @ApiModelProperty(value = "状态")
    @TableField("Status")
    private Integer Status;

    @ApiModelProperty(value = "是否删除")
    @TableField("IsDel")
    private Integer IsDel;

    @ApiModelProperty(value = "菜单系统名称")
    @TableField("MenuSysName")
    private String MenuSysName;

    @TableField("IconClass")
    private String IconClass;

    @ApiModelProperty(value = "菜单类型 1-管理平台 2-app菜单")
    @TableField("menusType")
    private Integer menusType;

    @ApiModelProperty("组件路径")
    private String component;

    @ApiModelProperty("重定向路径")
    @TableField("redirect")
    private String redirect;

    @ApiModelProperty(value = "备注")
    @TableField("Remarks")
    private String Remarks;


}
