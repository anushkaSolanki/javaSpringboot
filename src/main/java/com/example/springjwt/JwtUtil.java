package com.example.springjwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil {

    private String SECRET__KEY ="secret";
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());
	
    }

    private String createToken(Map<String, Object> claims, String subject)
    {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 100*60*60*10)).signWith(SignatureAlgorithm.HS512, SECRET__KEY).compact();
	
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET__KEY).parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token)
    {
        return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token)
    {
        return extractClaim( token,Claims::getExpiration);
    }
    private Boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails)
    {
        final String username= extractUsername(token);
        return (username.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }
}