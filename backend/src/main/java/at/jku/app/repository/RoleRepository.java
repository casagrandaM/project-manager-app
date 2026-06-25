package at.jku.app.repository;

import at.jku.app.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for accessing and managing {@link Role} entities.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

	/**
	 * Finds a {@link Role} by its name.
	 *
	 * @param name The role name
	 * @return The matching role, if found
	 */
	Optional<Role> findByName(String name);

}
