package cn.visolink.system.logs.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @author：sjl
 * @date： 2019/10/24 10:17
 */
@Data
@Accessors(chain = true)
@TableName("log")
@ApiModel(value = "Log日志监控对象", description = "")
public class Logs implements Serializable {

    private String id;
    //创建时间
    private Date createTime;

    //操作描述
    private String description;

    //异常信息
    private String exception;

    //日志类型
    private String logType;

    //错误方法

    private String method;

    //参数
    private String params;

    //请求ip
    private String requestId;

    //请求所用时间
    private Date time;

    //用户名
    private String useName;


}
