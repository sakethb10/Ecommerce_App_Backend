package com.springboot.ecomproj.security.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.springboot.ecomproj.security.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
	public static final Logger LOGGER=LoggerFactory.getLogger(JwtUtils.class);
	
	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;
	
	@Value("${spring.app.jwtExpirationMs}")
	private Integer jwtExpirationMs;
	
	public String getJwtFromHeader(HttpServletRequest request) {
		String bearerToken=request.getHeader("Authorization");
		LOGGER.debug("Authorization header: {}",bearerToken);
		if(bearerToken!=null&&bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
	
	public String generateTokenFromUsername(UserDetailsImpl userDetails) {
		String username=userDetails.getUsername();
		List<String> roles=userDetails.getAuthorities().stream().map((item)->item.getAuthority()).toList();
		return Jwts.builder().subject(username).claim("roles", roles).issuedAt(new Date()).expiration(new Date((new Date().getTime()+jwtExpirationMs))).signWith(key()).compact();
	}
	
	public String getUserNameFromJwtToken(String token) {
		return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();
	}
	
	public Boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
			return true;
		}catch(MalformedJwtException e) {
			LOGGER.error("Invalid JWT Token: {}", e.getMessage());
		}catch(ExpiredJwtException e) {
			LOGGER.error("JWT Token Is Expired: {}", e.getMessage());
		}catch(UnsupportedJwtException e) {
			LOGGER.error("JWT Token Is Unsupported: {}", e.getMessage());
		}catch(IllegalArgumentException e) {
			LOGGER.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}
}
