package at.jku.app.security.service;

import at.jku.app.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

/**
 * Service responsible for generating, parsing, and validating JWT tokens.
 * <p>
 * This service handles token creation with user claims and validation
 * using the configured signing key.
 */
@Service
public class JwtService {
	
	@Value("${app.jwt.secret}")
	private String secret;
	
	@Value("${app.jwt.expiration-ms}")
	private long expirationMs;

	/**
	 * Builds the signing key used for JWT generation and validation.
	 *
	 * @return The {@link SecretKey} used to sign and verify tokens
	 */
	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generates a JWT token for the given {@link User}.
	 *
	 * @param user The authenticated user
	 * @return The generated JWT token
	 */
	public String generateToken(User user) {
		Instant now = Instant.now();
		
		return Jwts.builder()
				.subject(user.getEmail())
				.claim("userId", user.getId())
				.claim("role", user.getRole().getName())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(expirationMs)))
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * Extracts the username (email) from a JWT token.
	 *
	 * @param token The JWT token
	 * @return The username stored in the token
	 */
	public String extractUsername(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}

	/**
	 * Validates a JWT token against the provided user details.
	 *
	 * @param token       The JWT token
	 * @param userDetails The user details to validate against
	 * @return {@code true} if the token is valid for the user, otherwise {@code false}
	 */
	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername());
	}
}
