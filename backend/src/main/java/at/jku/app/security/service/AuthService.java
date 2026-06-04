package at.jku.app.security.service;

import at.jku.app.entity.Role;
import at.jku.app.entity.User;
import at.jku.app.repository.RoleRepository;
import at.jku.app.repository.UserRepository;
import at.jku.app.security.data.OAuthAccount;
import at.jku.app.security.data.OAuthAccountRepository;
import at.jku.app.security.dto.AuthResponse;
import at.jku.app.security.dto.LoginRequest;
import at.jku.app.security.dto.OAuthUserInfo;
import at.jku.app.security.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
	
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final OAuthAccountRepository oauthAccountRepository;
	
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationConfiguration authenticationConfiguration;
	private final JwtService jwtService;
	
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new RuntimeException("Email already exists");
		}
		
		Role role = roleRepository.findByName("USER")
				.orElseThrow(() -> new IllegalStateException("Role USER does not exist in database - please insert the role first"));
		
		User user = new User();
		
		user.setName(request.name());
		user.setEmail(request.email());
		user.setPassword(passwordEncoder.encode(request.password()));
		user.setRole(role);
		user.setCreatedAt(LocalDateTime.now());
		
		userRepository.save(user);
		
		String token = jwtService.generateToken(user);
		return new AuthResponse(token);
	}
	
	public AuthResponse login(LoginRequest request) {
		try {
			AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
		} catch (BadCredentialsException e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
		}
		
		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		String token = jwtService.generateToken(user);
		
		return new AuthResponse(token);
	}
	
	public AuthResponse loginWithOAuth(OAuthUserInfo info) {
		
		User user = userRepository.findByEmail(info.email())
				.orElseGet(() -> createOAuthUser(info));
		
		oauthAccountRepository.findByProviderAndProviderUserId(info.provider(), info.providerUserId())
				.orElseGet(() -> createOAuthAccount(user, info));
		String token = jwtService.generateToken(user);
		
		return new AuthResponse(token);
	}
	
	private User createOAuthUser(OAuthUserInfo info) {
		
		Role role = roleRepository.findByName("USER")
				.orElseThrow(() -> new IllegalStateException("Role USER does not exist in database - please insert the role first"));
		
		User user = new User();
		
		user.setName(info.name());
		user.setEmail(info.email());
		user.setRole(role);
		user.setCreatedAt(LocalDateTime.now());
		
		return userRepository.save(user);
	}
	
	private OAuthAccount createOAuthAccount(User user, OAuthUserInfo info) {
		
		OAuthAccount account = new OAuthAccount();
		
		account.setUser(user);
		account.setProvider(info.provider());
		account.setProviderUserId(info.providerUserId());
		
		return oauthAccountRepository.save(account);
	}
}
