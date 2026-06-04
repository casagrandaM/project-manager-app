package at.jku.app.security.config;

import at.jku.app.security.service.OAuth2SuccessHandler;
import at.jku.app.security.service.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtFilter jwtFilter;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	
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
								"/oauth2/**",
								"/login/**"
						)
						.permitAll()
						.anyRequest()
						.authenticated()
				)
				.oauth2Login(oauth -> oauth.successHandler(oAuth2SuccessHandler))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
