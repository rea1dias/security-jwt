package com.disa.authservice.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtTokenUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.lifetime}")
    private Duration lifetime;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        claims.put("roles", roles);

        Date issuedDate = new Date();
        Date expirationDate = new Date(issuedDate.getTime() + lifetime.toMillis());
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(issuedDate)
                .expiration(expirationDate)
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        return getClaimsFromToken(token)
                .getSubject();
    }

    public List<String> getRoles(String token) {
        return getClaimsFromToken(token)
                .get("roles", List.class);
    }

    private Claims getClaimsFromToken(String token) {

        return Jwts.parser()
                .setSigningKey(secret)
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }

}
