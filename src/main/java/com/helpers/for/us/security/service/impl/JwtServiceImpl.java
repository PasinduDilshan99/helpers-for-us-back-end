    package com.you.and.me.security.service.impl;


    import com.you.and.me.security.model.CustomUserDetails;
    import com.you.and.me.security.model.InsertJwtTokenRequest;
    import com.you.and.me.security.model.User;
    import com.you.and.me.security.service.JwtService;
    import com.you.and.me.security.service.JwtTokenService;
    import io.jsonwebtoken.Claims;
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.io.Decoders;
    import io.jsonwebtoken.security.Keys;
    import jakarta.servlet.http.HttpServletRequest;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.ResponseCookie;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Service;
    import org.springframework.util.StringUtils;

    import javax.crypto.SecretKey;
    import java.time.Duration;
    import java.time.Instant;
    import java.util.*;
    import java.util.function.Function;

    @Service
    public class JwtServiceImpl implements JwtService {

        @Value("${jwt.secret}")
        private String secretKey;

        @Value("${jwt.expiration.time}")
        private long jwtExpirationMillis;

        @Value("${jwt.refresh.expiration.time}")
        private long refreshJwtExpirationMillis;

        @Value("${jwt.cookie.name}")
        private String accessCookieName;

        @Value("${jwt.refresh.cookie.name}")
        private String refreshCookieName;

        @Value("${jwt.cookie.max-age}")
        private long accessCookieMaxAge;

        @Value("${jwt.refresh.cookie.max-age}")
        private long refreshCookieMaxAge;

        private final JwtTokenService jwtTokenService;

        @Autowired
        public JwtServiceImpl(JwtTokenService jwtTokenService) {
            this.jwtTokenService = jwtTokenService;
        }

        @Override
        public String generateAccessToken(User user) {
            Map<String, Object> claims = new HashMap<>();
            String token = buildToken(claims, user.getUsername(), jwtExpirationMillis);
            InsertJwtTokenRequest insertJwtTokenRequest = new InsertJwtTokenRequest();
            insertJwtTokenRequest.setUserId(user.getId());
            insertJwtTokenRequest.setToken(token);
            insertJwtTokenRequest.setExpiresAt(Instant.now().plusMillis(jwtExpirationMillis));
            insertJwtTokenRequest.setIpAddress(null);
            insertJwtTokenRequest.setUserAgent(null);
            jwtTokenService.insertJwtToken(insertJwtTokenRequest);
            return token;
        }

        @Override
        public String generateRefreshToken(User user) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("type", "refresh");
            return buildToken(claims, user.getUsername(), refreshJwtExpirationMillis);
        }

        private String buildToken(Map<String, Object> claims, String subject, long expiryMs) {
            Instant now = Instant.now();

            return Jwts.builder()
                    .claims(claims)
                    .subject(subject)
                    .issuedAt(Date.from(now))
                    .expiration(Date.from(now.plusMillis(expiryMs)))
                    .signWith(getKey())
                    .compact();
        }

        private SecretKey getKey() {
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            return Keys.hmacShaKeyFor(keyBytes);
        }

        @Override
        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        @Override
        public Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }

        @Override
        public <T> T extractClaim(String token, Function<Claims, T> resolver) {
            final Claims claims = extractAllClaims(token);
            return resolver.apply(claims);
        }

        private Claims extractAllClaims(String token) {
            return Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

        @Override
        public boolean validateToken(String token, UserDetails userDetails) {
            try {
                final String username = extractUsername(token);
                return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            } catch (Exception ex) {
                return false;
            }
        }

        @Override
        public boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
        }

        @Override
        public boolean isRefreshToken(String token) {
            return "refresh".equals(extractClaim(token, claims -> claims.getOrDefault("type", "").toString()));
        }

        @Override
        public ResponseCookie buildAccessTokenCookie(String token) {
            return ResponseCookie.from(accessCookieName, token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofSeconds(accessCookieMaxAge))
                    .sameSite("None")
                    .domain(".felicitatrips.com")
                    .build();
        }

        @Override
        public ResponseCookie buildRefreshTokenCookie(String token) {
            return ResponseCookie.from(refreshCookieName, token)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(Duration.ofSeconds(refreshCookieMaxAge))
                    .sameSite("None")
                    .domain(".felicitatrips.com")
                    .build();
        }

        @Override
        public Optional<String> resolveAccessToken(HttpServletRequest request) {
            String header = request.getHeader(HttpHeaders.AUTHORIZATION);

            if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
                return Optional.of(header.substring(7));
            }

            return getCookieValue(request, accessCookieName);
        }

        @Override
        public Optional<String> resolveRefreshToken(HttpServletRequest request) {
            return getCookieValue(request, refreshCookieName);
        }

        @Override
        public boolean canRefresh(String refreshToken, CustomUserDetails userDetails) {
            return !isTokenExpired(refreshToken)
                    && validateToken(refreshToken, userDetails)
                    && isRefreshToken(refreshToken);
        }

        @Override
        public Instant getAccessExpiryInstant() {
            return Instant.now().plusMillis(jwtExpirationMillis);
        }

        @Override
        public Instant getRefreshExpiryInstant() {
            return Instant.now().plusMillis(refreshJwtExpirationMillis);
        }

        private Optional<String> getCookieValue(HttpServletRequest request, String name) {
            if (request.getCookies() == null) {
                return Optional.empty();
            }

            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    return Optional.ofNullable(cookie.getValue());
                }
            }

            return Optional.empty();
        }

        @Override
        public ResponseCookie buildLogoutAccessTokenCookie() {
            return ResponseCookie.from(accessCookieName, "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("Strict")
                    .domain(".felicitatrips.com")
                    .build();
        }

        @Override
        public ResponseCookie buildLogoutRefreshTokenCookie() {
            return ResponseCookie.from(refreshCookieName, "")
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(0)
                    .sameSite("NONE")
                    .domain(".felicitatrips.com")
                    .build();
        }
    }
