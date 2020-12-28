package cn.visolink.system.parameter.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * DictionaryVO对象
 * </p>
 *
 * @author autoJob
 * @since 2019-08-27
 */
@Data
@ApiModel(value = "Dictionary对象", description = "通用字典")
public class DictionaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "Code码值")
    private String dictCode;

    @ApiModelProperty(value = "名称")
    private String dictName;

    @ApiModelProperty(value ="媒体子类")
    private List MixDictionaryVO;
}