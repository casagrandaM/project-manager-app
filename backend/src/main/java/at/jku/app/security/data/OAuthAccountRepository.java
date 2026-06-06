package at.jku.app.security.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, Long> {
	
	Optional<OAuthAccount>
	findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);
}
