package cn.visolink;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/18 21:18
 * @Version 1.0
 **/
@Getter
@Setter
public class MsgClient  implements java.io.Serializable {
    @Excel(name = "序号", height = 20, width = 30, isImportField = "true_st")
    private String id;
    @Excel(name = "备注", height = 20, width = 30, isImportField = "true_st")
    private String remark;
    @Excel(name = "创建时间", height = 20, width = 30, isImportField = "true_st")
    private Date birthday;

    @Excel(name = "学生姓名", height = 20, width = 30, isImportField = "true_st")
    private String clientName;
    @Excel(name = "学生电话", height = 20, width = 30, isImportField = "true_st")
    private String clientPhone;
    @Excel(name = "创建人", height = 20, width = 30, isImportField = "true_st")
    private String createBy;


}
