package at.jku.app.security.dto;

/**
 * Data transfer object representing a login request.
 *
 * @param email    The user email address
 * @param password The user password
 */
public record LoginRequest(String email, String password) {
}
