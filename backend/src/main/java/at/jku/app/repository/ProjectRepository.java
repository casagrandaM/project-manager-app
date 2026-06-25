package at.jku.app.repository;

import at.jku.app.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link Project} entities.
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
}