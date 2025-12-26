package zhenghc.filter;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import zhenghc.common.BaseConstants;
import zhenghc.common.util.JwtUtil;
import zhenghc.common.util.RedisUtil;
import zhenghc.entity.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;  // 注入你的JwtUtil


    public BaseConstants baseConstants;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 获取token
        String token = request.getHeader("Authorization");
        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = token.substring(7);

        // 验证token是否过期
        if (jwtUtil.isTokenExpired(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 解析token获取用户信息
        Long userId;
        String username;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            throw new RuntimeException("token非法");
        }

        // TODO 从redis中获取完整的用户信息
        // 这里暂时创建一个简单的user对象
        User user = new User();
        user.setId((Long) RedisUtil.getKey(BaseConstants.USER_ID_KEY+userId));

        // 存入SecurityContextHolder
        //内部由ThreadLocal实现，保证每个用户取到自己的信息
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 放行
        filterChain.doFilter(request, response);
    }
}