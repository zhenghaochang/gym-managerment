package zhenghc.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zhenghc.common.properties.JwtProperties;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Autowired
    private JwtProperties jwtProperties;

    public static String createJwt(Map<String, Object> claims, long timeMill, String secretKey) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        Long expMills = System.currentTimeMillis()+timeMill;
        Date exp = new Date(expMills);

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8))
                .setExpiration(exp);

        return builder.compact();

    }

    public static Claims parseJwt(String secretKey,String jwt) {
        Claims claims = Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(jwt).getBody();
        return claims;
    }

    /**
     * 验证token是否过期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseJwt(token,jwtProperties.getSecretKey());
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 创建token（实例方法，使用配置文件中的secretKey）
     */
    public String createToken(Long userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);

        return createJwt(claims, jwtProperties.getTimeMill(), jwtProperties.getSecretKey());
    }

    /**
     * 创建token（实例方法，自定义claims）
     */
    public String createToken(Map<String, Object> claims) {
        return createJwt(claims, jwtProperties.getTimeMill(), jwtProperties.getSecretKey());
    }

    /**
     * 解析token（实例方法）
     */
    public Claims parseToken(String jwt) {
        return parseJwt(jwtProperties.getSecretKey(), jwt);
    }

    /**
     * 从token中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    /**
     * 从token中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("username", String.class);
    }


}
