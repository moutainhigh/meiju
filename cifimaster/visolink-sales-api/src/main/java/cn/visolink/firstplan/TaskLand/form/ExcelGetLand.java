package cn.visolink.firstplan.TaskLand.form;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(value = "excel文件导出", description = "")
public class ExcelGetLand extends Page {

    private String plan_node_id;

    private String plan_id;

    private String timeNode;

    private String tollerlist;

}
