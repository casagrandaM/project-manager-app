package at.jku.app.security.service;

import at.jku.app.entity.User;
import at.jku.app.repository.UserRepository;
import at.jku.app.security.data.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service for loading user-specific authentication data for Spring Security.
 */
@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * Loads a user by email for authentication purposes.
	 *
	 * @param email The user email address
	 * @return The {@link UserDetails} representation of the user
	 *
	 * @throws UsernameNotFoundException If no user with the given email exists
	 */
	@Override
	public UserDetails loadUserByUsername(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
		return new AppUserPrincipal(user);
	}
}
