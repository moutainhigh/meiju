package cn.visolink.exception;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author WCL
 * @date 2018-11-23
 * 统一异常处理
 */
@Getter
@Slf4j
public class BadRequestException extends RuntimeException{

    private Integer code = BAD_REQUEST.value();

    private Exception exception;

    public BadRequestException(String msg){
        super(msg);
    }

    public BadRequestException(Integer code){
        super();
        this.code=code;
    }
    public BadRequestException(Integer code,Exception e){
        super();
        log.error(e.getMessage(),e);
        this.exception=e;
        this.code=code;
    }

    public BadRequestException(HttpStatus code,String msg){
        super(msg);
        this.code = code.value();
    }
    public BadRequestException(int code,String msg){
        super(msg);
        this.code = code;
    }

    public BadRequestException(int code,String msg,Exception e){
        super(msg);
       log.error(e.getMessage(),e);
        this.code = code;
        this.exception=e;
    }


}
