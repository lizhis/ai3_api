package com.ai.basewebsocket.error;


import com.alibaba.fastjson2.JSONObject;
import com.ai.basecommon.core.vo.BaseVO;
import com.ai.basecommon.exception.StatusCodeEnum;
import com.ai.basecommon.utils.LogUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description
 * @Author 
 * 
 */
@Controller
public class ErrorController extends AbstractErrorController {

    public ErrorController() {
        super(new DefaultErrorAttributes());
    }

    @RequestMapping("/error")
    @ResponseBody
    public BaseVO getErrorPath(HttpServletRequest request, HttpServletResponse response) {

        String service = request.getHeader("service");
        int status = (int)request.getAttribute("jakarta.servlet.error.status_code");

        if(404 == status){
            return null;
        }

        Exception exception = (Exception)request.getAttribute("jakarta.servlet.error.exception");
        if(null == exception){
            return null;
        }
        Throwable throwable = exception.getCause();
        String msg = throwable.getMessage();

        String begin = msg.substring(0,1);
        if(!begin.equals("{")){
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }

        int code;
        if(null == service || "".equals(service)){
            response.setStatus(200);
        }

        try{
            JSONObject object = JSONObject.parseObject(msg);
            if(null == service || "".equals(service)){
                code = object.getInteger("code");
            }
            else{
                code = -10000;
            }

            String message = object.getString("message");
            LogUtil.log("系统出现异常："+message);
            return new BaseVO(code,message);
        }catch (Exception e){
            LogUtil.log("异常错误：" + e.getMessage());
            return new BaseVO(StatusCodeEnum.SYSTEM_ERROR);
        }
    }


    protected Throwable getCause(HttpServletRequest request)
    {
        Throwable error = (Throwable)request.getAttribute("jakarta.servlet.error.exception");
        if(null == error){
            //MVC有可能会封装异常成ServletException ,需要调用getCause获取真正的异常
            while (error instanceof ServletException && error.getCause() != null){
                error = ((ServletException) error).getCause();
            }
        }
        return error;
    }


}