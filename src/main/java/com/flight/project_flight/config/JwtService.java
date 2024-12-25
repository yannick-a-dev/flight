package com.flight.project_flight.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;
@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);  // Définition du logger

    private final String secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtService(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.accessToken.expiration}") long accessTokenExpiration,
            @Value("${jwt.refreshToken.expiration}") long refreshTokenExpiration) {
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    private Key getSigningKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    // **Générer un token d'accès**
    public String generateAccessToken(String username) {
        logger.info("Generating access token for username: {}", username);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // **Générer un token de rafraîchissement**
    public String generateRefreshToken(String username) {
        logger.info("Generating refresh token for username: {}", username);
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // **Extraire le username à partir du token**
    public String extractUsername(String token) {
        logger.debug("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    // **Extraire une réclamation spécifique**
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        logger.debug("Extracting all claims from token");
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // **Valider un token**
    public boolean isTokenValid(String token, String username) {
        logger.debug("Validating token for username: {}", username);
        final String extractedUsername = extractUsername(token);
        boolean isValid = (extractedUsername.equals(username) && !isTokenExpired(token));
        logger.info("Token validation result: {}", isValid);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            logger.warn("Token has expired");
        }
        return expired;
    }

    private Date extractExpiration(String token) {
        logger.debug("Extracting expiration date from token");
        return extractClaim(token, Claims::getExpiration);
    }

    // **Rafraîchir le token d'accès**
    public String refreshAccessToken(String refreshToken) {
        logger.info("Refreshing access token using refresh token: {}", refreshToken);
        if (isTokenExpired(refreshToken)) {
            logger.error("Refresh token is expired");
            throw new RuntimeException("Refresh token is expired");
        }
        String username = extractUsername(refreshToken);
        return generateAccessToken(username);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("Validating token against userDetails for username: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        logger.info("Token validation result: {}", isValid);
        return isValid;
    }

    public long getAccessTokenExpiry() {
        return accessTokenExpiration;
    }
}