package at.jku.app.security.dto;

/**
 * Data transfer object representing a user registration request.
 *
 * @param name     The username
 * @param email    The user email address
 * @param password The user password
 */
public record RegisterRequest(String name, String email, String password) {
}
