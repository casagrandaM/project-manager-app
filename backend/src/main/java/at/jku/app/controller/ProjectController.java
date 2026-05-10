package at.jku.app.controller;

import at.jku.app.dto.ProjectCreateDto;
import at.jku.app.dto.ProjectResponseDto;
import at.jku.app.dto.ProjectUpdateDto;
import at.jku.app.entity.Project;
import at.jku.app.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    private final ProjectService projectService;
    private static final Long CURRENT_USER_ID = 1L;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponseDto> getProjects() {
        return projectService.getProjectsForUser(CURRENT_USER_ID)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProjectResponseDto getProject(@PathVariable Long id) {
        return toDto(projectService.getProjectById(id));
    }

    @PostMapping
    public ProjectResponseDto createProject(@RequestBody ProjectCreateDto dto) {
        Project project = projectService.createProject(dto.title, dto.description, CURRENT_USER_ID);
        return toDto(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateDto dto) {
        try {
            Project project = projectService.updateProject(id, dto.title, dto.description, CURRENT_USER_ID);
            return ResponseEntity.ok(toDto(project));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
        try {
            projectService.deleteProject(id, CURRENT_USER_ID);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    private ProjectResponseDto toDto(Project project) {
        ProjectResponseDto dto = new ProjectResponseDto();
        dto.id = project.getId();
        dto.title = project.getTitle();
        dto.description = project.getDescription();
        dto.createdAt = project.getCreatedAt() != null ? project.getCreatedAt().toString() : null;
        if (project.getCreatedBy() != null) {
            dto.createdById = project.getCreatedBy().getId();
            dto.createdByName = project.getCreatedBy().getName();
            dto.isOwner = project.getCreatedBy().getId().equals(CURRENT_USER_ID);
        }
        return dto;
    }
}
