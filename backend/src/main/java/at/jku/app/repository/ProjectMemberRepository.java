package at.jku.app.repository;

import at.jku.app.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link ProjectMember} entities.
 */
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    /**
     * Finds all memberships belonging to a given project.
     *
     * @param projectId The project ID
     * @return The list of memberships for the project
     */
    List<ProjectMember> findByProjectId(Long projectId);

    /**
     * Finds all memberships belonging to a given user.
     *
     * @param userId The user ID
     * @return The list of memberships for the user
     */
    List<ProjectMember> findByUserId(Long userId);
}
