/**

 *
 * @author 陈志福
 * @version 1.0
 * @最后修改时间： 2024-12-10
 * @修改说明，用于开放免去jwt认证的路径
 * @description:用于jwt认证的
 */


package com.CloudApp.LoginAndRegister.Config;

import com.CloudApp.LoginAndRegister.Utils.JwtUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
//认证
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthorizationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 排除不需要 JWT 验证的路径
        if (request.getServletPath().matches(
                "^/api/user/login$|^/user/login$|^/api/register.*$|^/register.*$|^/api/admin/login$|^/admin/login$|^/api/auth/logout$|^/admin/.*$"
        )) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = request.getHeader("Authorization");
        logger.info("获得的token为:"+token);
        if (token == null || !token.startsWith("Bearer ")) {
            // 设置匿名用户身份，允许继续处理请求
            logger.info("请求未携带有效 JWT，设置为匿名用户");
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken("anonymousUser", null, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))
            );
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7); // 去掉 "Bearer " 前缀

        try {
            String username = jwtUtils.extractUsername(token);
            String role = jwtUtils.extractRole(token); // 提取 role 或 UserType

            if (username != null && role != null && jwtUtils.validateToken(token, username)) {
                // 设置认证信息到 SecurityContext，根据角色设置权限
                logger.info("JWT 验证成功，用户名: " + username + "，角色: " + role);

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        username, null, AuthorityUtils.createAuthorityList("ROLE_" + role.toUpperCase())
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                throw new Exception("无效的 JWT");
            }
        } catch (Exception e) {
            logger.error("JWT Token 验证失败", e);

            // 返回 401 Unauthorized 响应
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT 验证失败");
            return;
        }


        // 打印当前认证用户
        logger.info("当前认证用户: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 确保调用过滤器链的下一步
        filterChain.doFilter(request, response);
    }

}
