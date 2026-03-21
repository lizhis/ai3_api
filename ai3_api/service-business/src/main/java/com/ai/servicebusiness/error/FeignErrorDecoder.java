package com.ai.servicebusiness.error;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.exception.BaseException;
import com.ai.basecommon.exception.StatusCodeEnum;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @Description
 * @Author
 */
@Configuration
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        System.out.println("=== Feign调用失败详情 ===");
        System.out.println("方法: " + s);
        System.out.println("状态码: " + response.status());
        System.out.println("请求URL: " + response.request().url());

        // 输出请求详情
        System.out.println("请求方法: " + response.request().httpMethod());
        System.out.println("请求Headers: " + response.request().headers());

        // 输出响应详情
        System.out.println("响应Headers: " + response.headers());

        try {
            String body = Util.toString(response.body().asReader());
            System.out.println("响应Body: " + body);
        } catch (Exception e) {
            System.out.println("读取响应失败: " + e.getMessage());
        }
        System.out.println("=== 详情结束 ===");

        if(500 == response.status()){
            try {
                String body = Util.toString(response.body().asReader());
                JSONObject msgJson = JSON.parseObject(body);

                Integer status = 0;
                String msg;
                String codeStr = msgJson.getString("code");
                if(null != codeStr && !"".equals(codeStr)){
                    status = msgJson.getInteger("code");
                    msg = msgJson.getString("msg");
                }else{
                    String message = msgJson.get("message").toString();
                    JSONObject msgs = JSON.parseObject(message);
                    status = msgs.getInteger("code");
                    msg = msgs.getString("msg");
                }

                BaseException baseException = new BaseException();
                baseException.setCode(status);
                baseException.setMessage(msg);
                return new Exception(JSON.toJSONString(baseException));
            } catch (IOException e) {
            }
        }

        BaseException baseException = new BaseException();
        baseException.setCode(StatusCodeEnum.SERVICE_ERROR.getCode());
        baseException.setMessage(StatusCodeEnum.SERVICE_ERROR.getMsg());
        return new Exception(JSON.toJSONString(baseException)) ;
    }
}
