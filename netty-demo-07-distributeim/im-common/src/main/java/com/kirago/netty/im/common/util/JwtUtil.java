package com.kirago.netty.im.common.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class JwtUtil {

    private static final String SECRET_KEY = "!@#qweQWE";


    private final static long TOKEN_EXPIRE_MILLIS = 1000 * 60 * 60;

    public static String createToken(){
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTimeMillis))    // 设置签发时间
                .setExpiration(new Date(currentTimeMillis + TOKEN_EXPIRE_MILLIS))   // 设置过期时间
                .signWith(SignatureAlgorithm.HS256,generateKey())
                .compact();
    }

    public static String createToken(Map<String, Object> claimMap){
        long currentTimeMillis = System.currentTimeMillis();
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date(currentTimeMillis))    // 设置签发时间
                .setExpiration(new Date(currentTimeMillis + TOKEN_EXPIRE_MILLIS))   // 设置过期时间
                .addClaims(claimMap)
                .signWith(SignatureAlgorithm.HS256, generateKey())
                .compact();
    }

    public static boolean verifyToken(String token) {
        try {
            Jwts.parser().setSigningKey(generateKey()).parseClaimsJws(token);
            return true;
        } catch (Exception e){
            log.info(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, Object> parseToken(String token) {
        return Jwts.parser()  // 得到DefaultJwtParser
                .setSigningKey(generateKey()) // 设置签名密钥
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key generateKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }
}
