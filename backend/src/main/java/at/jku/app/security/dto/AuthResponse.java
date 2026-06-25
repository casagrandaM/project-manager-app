package at.jku.app.security.dto;

/**
 * Data transfer object representing an authentication response.
 *
 * @param token The authentication token (e.g. JWT)
 */
public record AuthResponse(String token) {
}
