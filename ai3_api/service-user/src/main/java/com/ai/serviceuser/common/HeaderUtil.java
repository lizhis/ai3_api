package com.ai.serviceuser.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public class HeaderUtil {


    public static Map<String, String> getAllHeaders() {
        HttpServletRequest request = getCurrentHttpRequest();
        Map<String, String> headers = new HashMap<>();

        if (request != null) {
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                headers.put(name, value);
            }
        }

        return headers;
    }

    public static String getHeader(String headerName) {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request != null) {
            return request.getHeader(headerName);
        }
        return null;
    }

    private static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }


}
