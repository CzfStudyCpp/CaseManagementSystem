/**
 *
 * @author czf
 * @version 1.0
 * @最后修改时间： 2024-12-10
 * @修改说明，用于springboot security的过滤配置，禁用了csrf
 * @description:安全配置
 */

package com.CloudApp.LoginAndRegister.Config;

import com.CloudApp.LoginAndRegister.Utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtUtils jwtUtils;

    @Autowired
    public SecurityConfig(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder())
                .withUser("user").password(passwordEncoder().encode("password")).roles("USER");
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 禁用 CSRF
//                .csrf(csrf -> csrf
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                )
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // 配置 CORS
                .authorizeRequests(auth -> auth
                        .requestMatchers("/api/**","/admin/**","/admin/**","/api/admin/login","/admin/","/register/**","/user/login","/user/**","/requirements/**").permitAll() // 开放的路径
                        .anyRequest().authenticated()) // 其他路径需认证
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthorizationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // 允许的前端地址
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 允许的请求方法
        configuration.setAllowedHeaders(List.of("*", "Authorization")); // 允许的请求头，包含 Authorization
        configuration.setAllowCredentials(true); // 允许发送认证信息
        configuration.setMaxAge(3600L); // 缓存时间

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}






//
//@Configuration
//public class SecurityConfig {
//
//    private final JwtUtils jwtUtils;
//
//    @Autowired
//    public SecurityConfig(JwtUtils jwtUtils) {
//        this.jwtUtils = jwtUtils;
//    }
//
//    // 配置密码编码器
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    // 配置认证管理器
//    @Bean
//    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
//        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
//        authenticationManagerBuilder.inMemoryAuthentication()
//                .passwordEncoder(passwordEncoder())
//                .withUser("user").password(passwordEncoder().encode("password")).roles("USER");
//        return authenticationManagerBuilder.build();
//    }
//
//    // 配置 HTTP 请求的安全性
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()  // 使用正确的 API
//                .requestMatchers("/api/login/**", "/register/**", "/admin/**").permitAll()  // 管理员登录接口公开访问
//                .anyRequest().authenticated()  // 其他接口都需要认证
//                .and();
//        // 添加 JWT 认证过滤器
//        http.addFilterBefore(new JwtAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(new JwtAuthorizationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}