package org.example.coursedesign.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Calendar;
import java.util.Map;

public class JwtUtils {

    private static final String KEY = "secret";

    public static String createToken(Map<String, Object> claims) {
        Calendar instance = Calendar.getInstance();
        // 20分后令牌token失效
        instance.add(Calendar.MINUTE,2000);
        return JWT.create()
                .withClaim("claims", claims)
                .withExpiresAt(instance.getTime())
                .sign(Algorithm.HMAC256(KEY));
    }

    public static Map<String, Object> verifyToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }

}
