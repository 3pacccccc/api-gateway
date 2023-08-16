package org.imooc.core.filter.User;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.imooc.common.constants.FilterConst;
import org.imooc.common.enums.ResponseCode;
import org.imooc.common.exception.ResponseException;
import org.imooc.core.context.GatewayContext;
import org.imooc.core.filter.Filter;
import org.imooc.core.filter.FilterAspect;

import javax.servlet.http.Cookie;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author maruimin
 * @date 2023/8/16 22:16
 */
@Slf4j
@FilterAspect(id = FilterConst.USER_AUTH_FILTER_ID,
        name = FilterConst.USER_AUTH_FILTER_NAME,
        order = FilterConst.USER_AUTH_FILTER_ORDER)
public class UserAuthFilter implements Filter {

    private static final String SECRET_KEY = "faewifheafewhefsfjkds";
    private static final String COOKIE_NAME = "user-jwt";

    /**
     * 过滤
     *
     * @param ctx 网关上下文
     * @throws Exception 异常
     */
    @Override
    public void doFilter(GatewayContext ctx) throws Exception {
        // 检查是否需要用户鉴权
        if (ctx.getRule().getFilterConfig(FilterConst.USER_AUTH_FILTER_ID) == null) {
            return;
        }
        String token = ctx.getRequest().getCookie(COOKIE_NAME).value();
        if (StringUtils.isBlank(token)) {
            throw new ResponseException(ResponseCode.UNAUTHORIZED);
        }

        try {
            // 解析用户id
            long userId = parseUserId(token);
            ctx.getRequest().setUserId(userId);
        } catch (Exception e) {
            throw new ResponseException(ResponseCode.UNAUTHORIZED);
        }
    }

    private long parseUserId(String token) {
        Jwt jwt = Jwts.parser().setSigningKey(SECRET_KEY).parse(token);
        return Long.parseLong(((DefaultClaims) jwt.getBody()).getSubject());
    }

    public static void main(String[] args) {
    }
}
