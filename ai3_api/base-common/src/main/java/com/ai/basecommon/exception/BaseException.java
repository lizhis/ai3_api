package com.ai.basecommon.exception;

import com.alibaba.fastjson2.JSON;

public class BaseException {

    private int code;
    private String message;

    public static void error(StatusCodeEnum statusCodeEnum) throws Exception
    {
        error(statusCodeEnum.getCode(), statusCodeEnum.getMsg());
    }

    public static void error(StatusCodeEnum statusCodeEnum,String mark) throws Exception
    {
        String msg = statusCodeEnum.getMsg();
        String msgs =  msg.replace("{}",mark);
        error(statusCodeEnum.getCode(), msgs);
    }

    public static void error(String message) throws Exception
    {
        error(-1,message);
    }

    public static void error(int code,String message) throws Exception
    {
        BaseException baseException = new BaseException();
        baseException.setCode(code);
        baseException.setMessage(message);
        throw new Exception(JSON.toJSONString(baseException)) ;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BaseException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

}