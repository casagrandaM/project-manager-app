package at.jku.app.service;

import at.jku.app.entity.ProjectMember;
import at.jku.app.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that manages the membership relationship between users and projects.
 */
@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }

    /**
     * Retrieves all project memberships in the system.
     *
     * @return The list of all project members
     */
    public List<ProjectMember> getAll() {
        return projectMemberRepository.findAll();
    }

    /**
     * Retrieves all members of a given project.
     *
     * @param projectId The project ID
     * @return The list of members belonging to the project
     */
    public List<ProjectMember> getByProjectId(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    /**
     * Adds (persists) a project membership.
     *
     * @param member The membership to add
     * @return The persisted {@link ProjectMember}
     */
    public ProjectMember addMember(ProjectMember member) {
        return projectMemberRepository.save(member);
    }

    /**
     * Removes a project membership by ID.
     *
     * @param id The membership ID
     */
    public void removeMember(Long id) {
        projectMemberRepository.deleteById(id);
    }
}