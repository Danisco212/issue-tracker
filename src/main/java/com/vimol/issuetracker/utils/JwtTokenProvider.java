package com.vimol.issuetracker.utils;

import com.vimol.issuetracker.dto.Token;
import com.vimol.issuetracker.entities.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;
    @Value("${sessionTimePki.app.jwtExpiration}")
    private long jwtExpirationInMs;
    @Value("${authentication-test.auth.refreshTokenExpirationMsec}")
    private Long refreshTokenExpirationMsec;
    @Autowired
    private MyUserDetails myUserDetails;
    @Autowired
    ObjectFactory<HttpSession> httpSessionFactory;
    
    HttpSession session = null;
    
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = myUserDetails.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            System.out.println("IIIIIIInvalid JWT token");
        } catch (ExpiredJwtException ex) {
        	return true;
        } catch (UnsupportedJwtException ex) {
        	System.out.println("Unsupported token");
        } catch (IllegalArgumentException ex) {
            System.out.println("Jwt claims string is empty");
        }
        return false;
    }

    public Token removeToken( ) {
        return new Token(Token.TokenType.ACCESS, null, 0l, null);
    }

    public Token generateToken(User user ) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("auth", user.getPosition());
        Date now = new Date();
        Long duration = now.getTime() + jwtExpirationInMs;
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        String token = Jwts.builder().setClaims(claims).setSubject((user.getEmail())).setIssuedAt(new Date())
                .setExpiration(expiryDate).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
        return new Token(Token.TokenType.ACCESS, token, duration, LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
    }
    public Token generateRefreshToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("auth", user.getPosition());
        Date now = new Date();
        Long duration = now.getTime() + refreshTokenExpirationMsec;
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMsec);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        String token = Jwts.builder().setClaims(claims).setSubject((user.getEmail())).setIssuedAt(new Date())
                .setExpiration(expiryDate).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
        return new Token(Token.TokenType.REFRESH, token, duration, LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
    }

}