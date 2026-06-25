package at.jku.app.security.data;

import at.jku.app.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Spring Security principal that wraps the {@link User} entity
 * and provides authentication details for the security context.
 */
public class AppUserPrincipal implements UserDetails {
	
	private final User user;
	
	public AppUserPrincipal(User user) {
		this.user = user;
	}

	/**
	 * Returns the authorities granted to the user based on their role.
	 *
	 * @return The collection of granted authorities
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
	}

	/**
	 * Returns the user's password.
	 *
	 * @return The encrypted password
	 */
	@Override
	public String getPassword() {
		return user.getPassword();
	}

	/**
	 * Returns the unique username (email address) used for authentication.
	 *
	 * @return The user email address
	 */
	@Override
	public String getUsername() {
		return user.getEmail();
	}

	/**
	 * Returns the underlying {@link User} entity.
	 *
	 * @return The user entity
	 */
	public User getUser() {
		return user;
	}

}
