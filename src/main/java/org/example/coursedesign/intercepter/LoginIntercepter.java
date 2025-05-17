package org.example.coursedesign.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.coursedesign.utils.JwtUtils;
import org.example.coursedesign.utils.ThreadlocalUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginIntercepter implements HandlerInterceptor {
    @Override
    //对未登陆的请求进行拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //令牌验证
        String token = request.getHeader("Authorization");

        try{
            Map<String,Object>claims = JwtUtils.verifyToken(token);
            ThreadlocalUtils.set(claims);
            //无错放行
            return true;
        }catch (Exception e){
            response.setStatus(401);
            System.out.println(e);
            return false;
        }
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadlocalUtils.remove();
    }
}
