package at.jku.app.security.dto;

import at.jku.app.security.data.AuthProvider;

/**
 * Data transfer object representing OAuth user information returned by an authentication provider.
 *
 * @param provider       The authentication provider
 * @param providerUserId The user ID provided by the OAuth provider
 * @param email          The user email address
 * @param name           The user display name
 */
public record OAuthUserInfo(AuthProvider provider, String providerUserId, String email, String name) {
}
