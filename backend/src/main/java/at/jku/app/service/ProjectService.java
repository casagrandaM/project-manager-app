package at.jku.app.service;

import at.jku.app.entity.Project;
import at.jku.app.entity.ProjectMember;
import at.jku.app.entity.User;
import at.jku.app.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that encapsulates the business logic for project management,
 * including project lifecycle operations and ownership/membership handling.
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final TaskRepository taskRepository;
    private final StatusHistoryRepository statusHistoryRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMemberRepository projectMemberRepository,
            TaskRepository taskRepository,
            StatusHistoryRepository statusHistoryRepository,
            TaskAssignmentRepository taskAssignmentRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
        this.taskRepository = taskRepository;
        this.statusHistoryRepository = statusHistoryRepository;
        this.taskAssignmentRepository = taskAssignmentRepository;
    }

    /**
     * Retrieves all projects in the system.
     *
     * @return The list of all projects
     */
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    /**
     * Retrieves all projects a given user is a member of.
     *
     * @param userId The user ID
     * @return The list of projects the user belongs to
     */
    public List<Project> getProjectsForUser(Long userId) {
        return projectMemberRepository.findByUserId(userId)
                .stream()
                .map(ProjectMember::getProject)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a project by ID.
     *
     * @param id The project ID
     * @return The project entity
     * @throws RuntimeException if no project with the given ID exists
     */
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    /**
     * Creates a new project and registers the creator as a member with
     * project manager privileges.
     *
     * @param title       The project title
     * @param description The project description
     * @param userId      The ID of the creating user (becomes the owner)
     * @return The persisted project entity
     */
    public Project createProject(String title, String description, Long userId) {
        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setCreatedAt(LocalDateTime.now());

        User user = new User();
        user.setId(userId);
        project.setCreatedBy(user);

        Project saved = projectRepository.save(project);

        ProjectMember member = new ProjectMember();
        member.setProject(saved);
        member.setUser(user);
        member.setProjectManager(true);
        projectMemberRepository.save(member);

        return saved;
    }

    /**
     * Updates the title and description of a project. Only the project owner
     * is permitted to perform this operation.
     *
     * @param id          The project ID
     * @param title       The new title
     * @param description The new description
     * @param userId      The ID of the user requesting the update
     * @return The updated project entity
     * @throws RuntimeException if the requesting user is not the project owner
     */
    public Project updateProject(Long id, String title, String description, Long userId) {
        Project project = getProjectById(id);
        if (!project.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the owner can edit this project");
        }
        project.setTitle(title);
        project.setDescription(description);
        return projectRepository.save(project);
    }

    /**
     * Deletes a project and all of its dependent data (status history, task
     * assignments, tasks and memberships) within a single transaction. Only the
     * project owner is permitted to perform this operation.
     *
     * @param id     The project ID
     * @param userId The ID of the user requesting the deletion
     * @throws RuntimeException if the requesting user is not the project owner
     */
    @Transactional
    public void deleteProject(Long id, Long userId) {
        Project project = getProjectById(id);
        if (!project.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the owner can delete this project");
        }

        statusHistoryRepository.deleteAll(statusHistoryRepository.findByTaskProjectId(id));
        taskAssignmentRepository.deleteAll(taskAssignmentRepository.findByTaskProjectId(id));
        taskRepository.deleteAll(taskRepository.findByProjectId(id));
        projectMemberRepository.deleteAll(projectMemberRepository.findByProjectId(id));
        projectRepository.deleteById(id);
    }

    /**
     * Retrieves a project by ID.
     *
     * @param id The project ID
     * @return The project entity
     * @throws RuntimeException if no project with the given ID exists
     */
    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    }
}
