package com.ai.servicebusiness.interceptor;


import com.ai.basecommon.utils.JwtUtil;
import com.ai.basecommon.utils.StringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Description
 * @Author  
 *
 */
public class Interceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String userId = request.getHeader("X-UserId");
        request.setAttribute("X-UserId",userId);

        String version = request.getHeader("X-Version");
        request.setAttribute("X-Version",version);

        String dvi = request.getHeader("X-Dvi");
        request.setAttribute("X-Dvi",dvi);

        String timestamp = request.getHeader("X-Timestamp");
        request.setAttribute("X-Timestamp",timestamp);

        String signature = request.getHeader("X-Signature");
        request.setAttribute("X-Signature",signature);

        String xSwagger = request.getHeader("X-From-Swagger");
        if(null != xSwagger && xSwagger.equals("true")){
            request.setAttribute("X-From-Swagger",xSwagger);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //controller处理完毕后 调用这里
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //页面渲染完毕后调用这里
    }
}
