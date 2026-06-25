package at.jku.app.security.dto;

/**
 * Data transfer object representing an email entry from GitHub user profile data.
 *
 * @param email    The email address
 * @param primary  The flag indicating if this is the primary email
 * @param verified The flag indicating if the email is verified
 */
public record GithubEmailDto(String email, boolean primary, boolean verified) {
}
