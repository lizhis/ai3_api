package com.ai.basecommon.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description
 * @Author  
 *
 */
public class JwtUtil {

    public static final String ROLE_REFRESH_TOKEN = "ROLE_REFRESH_TOKEN";

    private static final String CLAIM_KEY_USER_ID = "userId";

    private static Map<String, String> tokenMap = new ConcurrentHashMap<>(32);
/*

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration:7200}")
    private Long access_token_expiration;

    @Value("${jwt.expiration:7200}")
    private Long refresh_token_expiration;
*/
    private static final Long access_token_expiration = 2592000L;

    private static final String secret = "1qaz@WSX#EDC$RFVa";


    public static Long getUserIdFromToken(String token) {
        Long userId = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            userId = Long.parseLong(String.valueOf(claims.get(CLAIM_KEY_USER_ID)));
        } catch (Exception e) {
            return null;
        }
        return userId;
    }

    public static Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = getClaimsFromToken(token);
            created = claims.getIssuedAt();
        } catch (Exception e) {
            created = null;
        }
        return created;
    }


    public static String generateToken(Long userId) {
        if(null == userId || userId < 1){
            return null;
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        return createToken(userId.toString(),claims,null);
    }

    public static String generateToken(Long userId,int expire) {
        if(null == userId || userId < 1){
            return null;
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userId);
        Long time = expire <= 0 ? access_token_expiration : expire;
        return createToken(userId.toString(),claims,time);
    }

    public static Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }


    public static Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = getCreatedDateFromToken(token);
        return !isCreatedBeforeLastPasswordReset(created, lastPasswordReset)
                && (!isTokenExpired(token));
    }

    public static String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            refreshedToken = createToken(username,claims,access_token_expiration);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }


    public static void putToken(String userName, String token) {
        tokenMap.put(userName, token);
    }

    public static void deleteToken(String userName) {
        tokenMap.remove(userName);
    }

    public static boolean containToken(String userName, String token) {
        if (userName != null && tokenMap.containsKey(userName) && tokenMap.get(userName).equals(token)) {
            return true;
        }
        return false;
    }

    private static Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private static Date generateExpirationDate(Long expire) {
        if(null == expire){
            return new Date(System.currentTimeMillis() + access_token_expiration * 1000);
        }else{
            return new Date(System.currentTimeMillis() + expire * 1000);
        }
    }

    private static Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private static Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    private static String createToken(String subject,Map<String, Object> claims,Long expire) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(generateExpirationDate(expire))
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }


}
