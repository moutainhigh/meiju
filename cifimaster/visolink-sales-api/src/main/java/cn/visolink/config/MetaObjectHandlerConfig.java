package cn.visolink.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * mybatisplus 公共字段自动填充处理类
 *
 * @author yangjie
 * @date 2020-5-15
 */
@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        Date now = new Date();
        setFieldValByName("isdel", 0, metaObject);
        this.setFieldValByName("editTime", now, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Date now = new Date();
        this.setFieldValByName("editTime", now, metaObject);
    }
}
