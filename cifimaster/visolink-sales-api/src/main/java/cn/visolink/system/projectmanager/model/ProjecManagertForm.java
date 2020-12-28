package cn.visolink.system.projectmanager.model;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "project对象", description = "")
public class ProjecManagertForm extends Page {
    private static final long serialVersionUID = 1L;

    private String projectStatus;

    private String status;

    private String projectName;

    private String userId;
}
