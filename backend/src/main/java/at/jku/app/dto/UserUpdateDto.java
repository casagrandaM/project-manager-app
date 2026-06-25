package at.jku.app.dto;

/**
 * Data transfer object containing user update information.
 *
 * @param name  The username
 * @param email The user email address
 */
public record UserUpdateDto(String name, String email) {
}
