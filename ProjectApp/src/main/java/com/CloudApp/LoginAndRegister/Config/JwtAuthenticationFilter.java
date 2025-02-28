/**

 *
 * @author czf
 * @version 1.0
 * @最后修改时间： 2024-12-8
 * @修改说明，实现成功认证后生成jwt
 * @description:用于jwt链式生成
 */
package com.CloudApp.LoginAndRegister.Config;

import com.CloudApp.LoginAndRegister.Utils.JwtUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    // 重写 successfulAuthentication 方法
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        // 提取用户名
        String username = authResult.getName();

        // 提取用户角色（将角色转换为逗号分隔的字符串）
        String roles = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 生成包含用户名和角色信息的 JWT
        String token = jwtUtils.generateToken(username, roles);

        // 将 token 放到响应头中返回给客户端
        response.addHeader("Authorization", "Bearer " + token);

        // 调试日志
        System.out.println("Generated JWT Token: " + token);

        // 继续执行过滤链
        super.successfulAuthentication(request, response, chain, authResult);
    }
}

