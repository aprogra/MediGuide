package com.mediguide.common.exception;

import com.mediguide.common.entity.Result;
import lombok.Getter;
import lombok.Setter;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 
 * @author MediGuide
 * @version 1.0
 */
@Getter
@Setter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 错误消息
     */
    private String message;
    
    /**
     * 错误详情
     */
    private Object details;

    /**
     * 构造函数
     * 
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.code = Result.FAIL_CODE;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param details 错误详情
     */
    public BusinessException(Integer code, String message, Object details) {
        super(message);
        this.code = code;
        this.message = message;
        this.details = details;
    }

    /**
     * 构造函数
     * 
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = Result.FAIL_CODE;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param cause 异常原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     * 
     * @param code 错误码
     * @param message 错误消息
     * @param details 错误详情
     * @param cause 异常原因
     */
    public BusinessException(Integer code, String message, Object details, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.details = details;
    }

    /**
     * 转换为Result对象
     * 
     * @return Result对象
     */
    public Result<Object> toResult() {
        return Result.fail(this.code, this.message, this.details);
    }

    /**
     * 获取异常堆栈信息
     * 
     * @return 异常堆栈字符串
     */
    public String getStackTraceString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BusinessException: ").append(this.getMessage()).append("\n");
        StackTraceElement[] stackTrace = this.getStackTrace();
        for (StackTraceElement element : stackTrace) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", details=" + details +
                '}';
    }
}