package cn.visolink.exception.handler;

import cn.visolink.exception.BadRequestException;
import cn.visolink.exception.ResultBody;
import cn.visolink.exception.ResultUtil;
import cn.visolink.utils.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * @author WCL
 * @date 2018-11-23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有不可知的异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public ResultBody handleException(Throwable e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        ApiError apiError = new ApiError(BAD_REQUEST.value(), e.getMessage());
        return buildResponseEntity(apiError);
    }

    /**
     * 处理 接口无权访问异常AccessDeniedException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResultBody handleAccessDeniedException(AccessDeniedException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        ApiError apiError = new ApiError(FORBIDDEN.value(), e.getMessage());
        return buildResponseEntity(apiError);
    }

    /**
     * 处理自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BadRequestException.class)
    public ResultBody badRequestException(BadRequestException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        ApiError apiError = new ApiError(e.getCode(), e.getMessage());

        return buildResponseEntity(apiError);
    }


    /**
     * 处理所有接口数据验证异常
     *
     * @param e
     * @returns
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultBody handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        String[] str = e.getBindingResult().getAllErrors().get(0).getCodes()[1].split("\\.");
        StringBuffer msg = new StringBuffer(str[1] + ":");
        msg.append(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        ApiError apiError = new ApiError(BAD_REQUEST.value(), msg.toString());
        return buildResponseEntity(apiError);
    }

    /**
     * 统一返回
     *
     * @param apiError
     * @return
     */
    private ResultBody buildResponseEntity(ApiError apiError) {
        return ResultUtil.error(apiError.getCode(), apiError.getMessages());
    }

    /**
     * 其他未知异常
     *
     * @param ex 异常对象
     * @return ResultBody对象
     */
    @ExceptionHandler(value = Exception.class)
    public ResultBody handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return ResultUtil.error(500, "服务器繁忙，请稍后重试");
    }
}
