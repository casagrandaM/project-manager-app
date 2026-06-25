package at.jku.app.security.config;

import at.jku.app.security.service.CustomAuthorizationRequestResolver;
import at.jku.app.security.service.OAuth2SuccessHandler;
import at.jku.app.security.service.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Security configuration for application authentication and authorization.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtFilter jwtFilter;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final ClientRegistrationRepository clientRegistrationRepository;

	/**
	 * Configures HTTP security including JWT authentication, OAuth2 login,
	 * public endpoints and request authorization rules.
	 *
	 * @param http The {@link HttpSecurity} configuration
	 * @return The configured {@link SecurityFilterChain}
	 *
	 * @throws Exception If the security configuration cannot be built
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
				.cors(cors -> {})
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(
								(request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
						)
				)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**")
						.permitAll()
						.requestMatchers(
								"/auth/login",
								"/auth/register",
								"/oauth2/**"
						)
						.permitAll()
						.anyRequest()
						.authenticated()
				)
				.oauth2Login(oauth -> oauth
						.authorizationEndpoint(auth -> auth.authorizationRequestResolver(new CustomAuthorizationRequestResolver(clientRegistrationRepository)))
						.successHandler(oAuth2SuccessHandler)
				)
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
