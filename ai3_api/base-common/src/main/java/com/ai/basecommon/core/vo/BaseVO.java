package com.ai.basecommon.core.vo;

import com.ai.basecommon.exception.StatusCodeEnum;

public class BaseVO<T> {

    //返回码
    private int code;

    //返回结果描述
    private String msg;

    private int total;

    public BaseVO() {
    }

    /**
     * 返回内容
     */
    private T data;


    public int getCode() {
        return code;
    }

    public void setCode(StatusCodeEnum status) {
        this.code = status.getCode();
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseVO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseVO(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public BaseVO(StatusCodeEnum status) {
        this.code = status.getCode();
        this.msg = status.getMsg();
    }

    public BaseVO(StatusCodeEnum status, T data) {
        this.code = status.getCode();
        this.msg = status.getMsg();
        this.data = data;
    }

    public static <V> BaseVO ok(V content) {
        return new BaseVO(StatusCodeEnum.SUCCESS, content);
    }

    public static <V> BaseVO ok(V content,int total) {
        BaseVO vo = new BaseVO(StatusCodeEnum.SUCCESS, content);
        vo.setTotal(total);
        return vo;
    }

    public static BaseVO ok() {
        return new BaseVO(StatusCodeEnum.SUCCESS);
    }


    public static BaseVO bool(boolean value){
        if(!value){
            return BaseVO.error();
        }
        return BaseVO.ok();
    }

    public static BaseVO error() {
        return new BaseVO(StatusCodeEnum.ERROR);
    }

    public static BaseVO error(StatusCodeEnum error) {
        return new BaseVO(error);
    }


    public static BaseVO error(String msg) {
        BaseVO vo = new BaseVO();
        vo.setCode(StatusCodeEnum.ERROR);
        vo.setMsg(msg);
        return vo;
    }


    @Override
    public String toString() {
        return "BaseVO{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}
