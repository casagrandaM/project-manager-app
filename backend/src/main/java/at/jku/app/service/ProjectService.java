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

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<Project> getProjectsForUser(Long userId) {
        return projectMemberRepository.findByUserId(userId)
                .stream()
                .map(ProjectMember::getProject)
                .collect(Collectors.toList());
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

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

    public Project updateProject(Long id, String title, String description, Long userId) {
        Project project = getProjectById(id);
        if (!project.getCreatedBy().getId().equals(userId)) {
            throw new RuntimeException("Only the owner can edit this project");
        }
        project.setTitle(title);
        project.setDescription(description);
        return projectRepository.save(project);
    }

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

    public Project getById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    }
}
