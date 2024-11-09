package com.invoice.approval.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.invoice.approval.entity.TokenVO;
import com.invoice.approval.entity.UserVO;
import com.invoice.approval.repo.TokenRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;

@Service
public class TokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

	@Value("${app.auth.tokenKey:efiterpkey}")
	private String tokenKey;

	@Value("${app.auth.tokenSecret:efiterpcret}")
	private String tokenSecret;

	@Value("${app.auth.tokenExpirationMsec:1500000}") 
	private long tokenExpInMSec;

	@Value("${app.auth.refreshtokenExpirationMsec:3000000}")
	private long refreshTokenExpInMSec;

	private byte[] hmacSHA512Byte;

	private static final String HMAC_SHA512 = "HmacSHA512";

	@Autowired
	TokenRepo tokenRepo;

	@PostConstruct  
	public void init() {
		hmacSHA512Byte = generateHmacSHA512Key(tokenKey, tokenSecret);
	}

	private byte[] generateHmacSHA512Key(String key, String secret) {
		try {
			Mac sha512Hmac = Mac.getInstance(HMAC_SHA512);
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA512);
			sha512Hmac.init(keySpec);
			return sha512Hmac.doFinal(secret.getBytes(StandardCharsets.UTF_8));
		} catch (InvalidKeyException | NoSuchAlgorithmException e) {
			logger.error("Error while generating HMAC SHA-512 key: {}", e.getMessage());
			throw new RuntimeException("Error initializing HMAC SHA-512 key", e);
		}
	}

	private TokenVO buildAndSaveTokenVO(long userId) {
		Date now = new Date();
		Date refreshTokenExpiryDate = new Date(now.getTime() + refreshTokenExpInMSec);
		return tokenRepo.save(TokenVO.builder()
				.id(UUID.randomUUID().toString())
				.userId(userId)
				.createdDate(now)
				.expDate(refreshTokenExpiryDate)
				.build());
	}

	private String createJwtToken(long userId, String subject, Claims claims) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + tokenExpInMSec);
		Key hmacSHA512Key = new SecretKeySpec(hmacSHA512Byte, HMAC_SHA512);
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(now)
				.setExpiration(expiryDate)
				.signWith(hmacSHA512Key, SignatureAlgorithm.HS512)
				.compact();
	}

	public TokenVO createToken(Authentication authentication) {
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		return createToken(userPrincipal.getUserId(), userPrincipal.getUserName());
	}

	public TokenVO createToken(long userId) {
		return createToken(userId, String.valueOf(userId));
	}

	public TokenVO createToken(long userId, String userName) {
		Claims claims = Jwts.claims().setSubject(userName);
		claims.put("id", userId);
		TokenVO tokenVO = buildAndSaveTokenVO(userId);
		String token = createJwtToken(userId, userName, claims);
		tokenVO.setToken(token);
		return tokenVO;
	}

	public String getUserIdFromToken(String token) {
		Key hmacSHA512Key = new SecretKeySpec(hmacSHA512Byte, HMAC_SHA512);
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(hmacSHA512Key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		return claims.get("id").toString();
	}

	public boolean validateToken(String authToken) {
		try {
			Key hmacSHA512Key = new SecretKeySpec(hmacSHA512Byte, HMAC_SHA512);
			Jwts.parserBuilder()
					.setSigningKey(hmacSHA512Key)
					.build()
					.parseClaimsJws(authToken);
			return true;
		} catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
			logger.error("Invalid JWT token: {}", ex.getMessage());
		}
		return false;
	}

	public TokenVO createRefreshToken(TokenVO tokenVO, UserVO userVO) {
		Claims claims = Jwts.claims().setSubject(userVO.getUserName());
		claims.put("id", userVO.getId());
		String token = createJwtToken(userVO.getId(), userVO.getUserName(), claims);
		tokenVO.setToken(token);
		tokenVO.setExpDate(new Date(System.currentTimeMillis() + refreshTokenExpInMSec));
		return tokenRepo.save(tokenVO);
	}

	public long getTokenExpiryDuration(String token) {
		Key hmacSHA512Key = new SecretKeySpec(hmacSHA512Byte, HMAC_SHA512);
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(hmacSHA512Key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		Date expiration = claims.getExpiration();
		return expiration.getTime() - System.currentTimeMillis();
	}

	public long getTokenExpInMSec() {
		return tokenExpInMSec;
	}
}
