package com.ai.serviceuser.interceptor;


import com.ai.serviceuser.common.RedisUtilX;
import com.ai.serviceuser.mapper.UserLoginMapper;
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

    private final RedisUtilX redisUtilX;
    private final UserLoginMapper userLoginMapper;

    public Interceptor(RedisUtilX redisUtilX, UserLoginMapper userLoginMapper) {
        this.redisUtilX = redisUtilX;
        this.userLoginMapper = userLoginMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //x-forwarded-prefix: /user
        //x-forwarded-port: 9527
        //x-forwarded-host: 192.168.0.123:9527
        //String url = request.getRequestURI();

        /*
        String requestSource = request.getHeader("X-Request-Source");
        if(!"gateway".equals(requestSource)){
            response.setHeader("Access-Control-Allow-Origin", "*");//* or origin as u prefer
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With, token");
            response.setHeader("XDomainRequestAllowed","1");
            response.setHeader("Access-Control-Expose-Headers","download-status,download-filename,download-message");

            String token = request.getHeader("X-Token");
            if(!StringUtil.isEmpty(token)){
                Long userId = JwtUtil.getUserIdFromToken(token);
                if(null == userId || userId < 1){
                    request.setAttribute("userId",null);
                    return true;
                }
                request.setAttribute("userId",userId);
                request.setAttribute("X-Token",token);
            }
        }
*/

        String userId = request.getHeader("X-UserId");
        request.setAttribute("X-UserId",userId);

        String version = request.getHeader("X-Version");
        request.setAttribute("X-Version",version);

        String dvi = request.getHeader("X-Dvi");
        request.setAttribute("X-Dvi",dvi);

        String oaid = request.getHeader("X-Oaid");
        request.setAttribute("X-Oaid",oaid);

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
