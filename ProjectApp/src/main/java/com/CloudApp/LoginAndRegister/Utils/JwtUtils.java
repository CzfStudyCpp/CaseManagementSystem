package com.CloudApp.LoginAndRegister.Utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private final String SECRET_KEY = "DBprojecet";

    // 生成 JWT token
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .setSubject(username) // 设置用户名
                .claim("role", role)  // 添加自定义 Claim，存储用户角色
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1小时过期
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 使用 HS256 算法签名
                .compact();
    }

    // 从 JWT 中提取 Claims（包括 username 和 role）
    public Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    // 从 JWT 中提取用户名
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 从 JWT 中提取角色
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class); // 提取 role 的值
    }

    // 检查 JWT 是否过期
    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // 验证 JWT 是否有效
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }
}
