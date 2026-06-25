package at.jku.app.security.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing and managing {@link OAuthAccount} entities.
 */
public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {

	/**
	 * Finds an OAuth account by authentication provider and provider-specific user ID.
	 *
	 * @param provider       The authentication provider
	 * @param providerUserId The user ID assigned by the provider
	 * @return The matching OAuth account, if found
	 */
	Optional<OAuthAccount> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}