package at.jku.app.security.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom {@link OAuth2AuthorizationRequestResolver} that modifies the default
 * OAuth2 authorization request to include additional provider-specific parameters.
 * <p>
 * This implementation adds support for forcing account selection during OAuth login.
 */
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {
	
	private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;
	
	public CustomAuthorizationRequestResolver(ClientRegistrationRepository repo) {
		this.defaultResolver =
				new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
	}

	/**
	 * Resolves and customizes the OAuth2 authorization request.
	 *
	 * @param request The incoming HTTP request
	 * @return The customized {@link OAuth2AuthorizationRequest}, or {@code null} if none exists
	 */
	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest req = defaultResolver.resolve(request);
		return customize(req);
	}

	/**
	 * Resolves and customizes the OAuth2 authorization request for a specific client registration.
	 *
	 * @param request              The incoming HTTP request
	 * @param clientRegistrationId The OAuth2 client registration ID
	 * @return The customized {@link OAuth2AuthorizationRequest}, or {@code null} if none exists
	 */
	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		OAuth2AuthorizationRequest req = defaultResolver.resolve(request, clientRegistrationId);
		return customize(req);
	}

	/**
	 * Customizes the OAuth2 authorization request by adding additional parameters.
	 * <p>
	 * Adds {@code prompt=select_account} to force account selection on login.
	 *
	 * @param req The original authorization request
	 * @return The modified request, or {@code null} if input was {@code null}
	 */
	private OAuth2AuthorizationRequest customize(OAuth2AuthorizationRequest req) {
		if (req == null) return null;
		
		Map<String, Object> extra = new HashMap<>(req.getAdditionalParameters());
		
		extra.put("prompt", "select_account");
		
		return OAuth2AuthorizationRequest.from(req)
				.additionalParameters(extra)
				.build();
	}
}