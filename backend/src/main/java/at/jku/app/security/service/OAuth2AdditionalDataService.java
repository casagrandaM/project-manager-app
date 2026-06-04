package at.jku.app.security.service;

import at.jku.app.security.dto.GithubEmailDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OAuth2AdditionalDataService {
	
	private final RestClient restClient = RestClient.builder().build();
	
	public String getGithubPrimaryEmail(String accessToken) {
		List<GithubEmailDto> emails = restClient.get()
						.uri("https://api.github.com/user/emails")
						.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
						.retrieve()
						.body(new ParameterizedTypeReference<>() {});
		
		if (emails == null) {
			throw new IllegalStateException(
					"No email information returned from GitHub"
			);
		}
		
		return emails.stream()
				.filter(GithubEmailDto::primary)
				.filter(GithubEmailDto::verified)
				.map(GithubEmailDto::email)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No verified primary email found"));
	}
}
