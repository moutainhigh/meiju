package cn.visolink.exception.handler;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @date 2018-11-23
 */
@Data
class ApiError {

    private long code;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String messages;

    private ApiError() {
    }

    public ApiError(long code,String messages) {
        this();
        this.code = code;
        this.messages = messages;
    }
    private ApiError(String message) {
        this();
        this.messages = messages;
    }

}


