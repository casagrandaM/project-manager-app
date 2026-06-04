package at.jku.app.security.service;

import at.jku.app.entity.User;
import at.jku.app.repository.UserRepository;
import at.jku.app.security.data.AppUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailsService {
	
	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
		return new AppUserPrincipal(user);
	}
}
