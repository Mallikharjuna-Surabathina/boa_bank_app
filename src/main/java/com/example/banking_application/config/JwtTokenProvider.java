package com.example.banking_application.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
/*
@Value("${app.jwt.secret}")
    private String jwtSecret;

@Value("${app.jwt.expiration}")
 private long jwtExpirationDate;

public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    Date currentDate = new Date();
    Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(currentDate)
            .setExpiration(expireDate)
            .signWith(key())
            .compact();
}
    private Key key(){
       byte[] bytes =Decoders.BASE64.decode(jwtSecret);
       return Keys.hmacShaKeyFor(bytes);

    }

    public String getUsername(String token){
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(key())
            .build()
            .parseClaimsJws(token)
            .getBody();
    return claims.getSubject();

    }
    public boolean validateToken(String token){
    try{
        Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parse(token);
              return true;

    } catch (ExpiredJwtException | IllegalArgumentException | SignatureException | MalformedJwtException e) {
        throw new RuntimeException(e);
    }
    }*/

    private String jwtSecret = "rvW6iJCO+CS42x4ew/usocVIhH2cQCKOxoHuQcXTpTk="; // Replace with a valid Base64 key
    private long jwtExpirationDate = 86400000; // Example: 1 day in milliseconds

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key())
                .compact();
    }

    private Key key() {
        byte[] bytes = Decoders.BASE64.decode(jwtSecret); // Ensure jwtSecret is Base64 encoded
        return Keys.hmacShaKeyFor(bytes);
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("Token expired");
        } catch (MalformedJwtException e) {
            System.out.println("Invalid token format");
        } catch (SignatureException e) {
            System.out.println("Invalid token signature");
        } catch (IllegalArgumentException e) {
            System.out.println("Empty or null token");
        }
        return false;
    }
}


