package at.jku.app.security.data;

import at.jku.app.entity.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "oauth_accounts")
public class OAuthAccount {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "provider", nullable = false)
	private AuthProvider provider;
	
	@Column(name = "provider_user_id", nullable = false)
	private String providerUserId;
}
