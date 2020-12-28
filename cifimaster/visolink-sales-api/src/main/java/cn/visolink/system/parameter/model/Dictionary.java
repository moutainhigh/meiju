package cn.visolink.system.parameter.model;

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
 * 字典表
 * </p>
 *
 * @author autoJob
 * @since 2019-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_dictionary")
@ApiModel(value = "Dictionary对象", description = "通用字典")
public class Dictionary implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID",type = IdType.INPUT)
    private String id;

    @TableField("PID")
    private String pid;

    @TableField("ListIndex")
    private Integer listIndex;

    @TableField("DictCode")
    private String dictCode;

    @TableField("DictName")
    private String dictName;

    @ApiModelProperty(value = "1----value，2----list，3----tree")
    @TableField("DictType")
    private Boolean dictType;

    @TableField("Levels")
    private Integer levels;

    @TableField("Remark")
    private String remark;

    @TableField("IsReadOnly")
    private Boolean isReadOnly;

    @TableField("FullPath")
    private String fullPath;

    @TableField("Ext1")
    private String ext1;

    @TableField("Ext2")
    private String ext2;

    @TableField("Ext3")
    private String ext3;

    @TableField("Ext4")
    private String ext4;

    @TableField("AuthCompanyID")
    private String authCompanyID;

    @TableField("ProductID")
    private String productID;

    @TableField("Creator")
    private String creator;

    @TableField("CreateTime")
    private Date createTime;

    @TableField("Editor")
    private String editor;

    @TableField("EditTime")
    private Date editTime;

    @TableField("Status")
    private Boolean status;

    @TableField("IsDel")
    private Boolean isDel;

    @TableField("ProjectID")
    private String projectID;

    @TableField("DictionaryLevel")
    private Boolean dictionaryLevel;


}
