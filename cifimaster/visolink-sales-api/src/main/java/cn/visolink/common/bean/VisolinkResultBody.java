package cn.visolink.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author wcl
 * @Description //TODO
 * @Date 2019/7/8 17:41
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisolinkResultBody<T> implements Serializable {

    private int code=200;

    private Object result;

    private String messages;

}
