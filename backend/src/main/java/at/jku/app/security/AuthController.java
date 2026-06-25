package at.jku.app.security;

import at.jku.app.dto.ProjectResponseDto;
import at.jku.app.dto.RoleDto;
import at.jku.app.dto.TaskResponseDto;
import at.jku.app.dto.UserDto;
import at.jku.app.entity.*;
import at.jku.app.security.data.AppUserPrincipal;
import at.jku.app.security.dto.AuthResponse;
import at.jku.app.security.dto.LoginRequest;
import at.jku.app.security.dto.RegisterRequest;
import at.jku.app.security.service.AuthService;
import at.jku.app.service.ProjectService;
import at.jku.app.service.TaskService;
import at.jku.app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for authentication and retrieving the currently authenticated user.
 * <p>
 * Provides endpoints for user registration, login, and fetching the current user profile.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	
	private final AuthService authService;
	
	private final UserService userService;
	private final ProjectService projectService;
	private final TaskService taskService;

	/**
	 * Registers a new user, logs them in and returns an authentication response.
	 *
	 * @param request The registration request containing user credentials
	 * @return The authentication response containing a JWT token
	 */
	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
		return ResponseEntity.ok(authService.register(request));
	}

	/**
	 * Authenticates a user using email and password credentials.
	 *
	 * @param request The login request containing credentials
	 * @return The authentication response containing a JWT token
	 */
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(authService.login(request));
	}

	/**
	 * Returns the currently authenticated user.
	 *
	 * @param authentication The Spring Security authentication object
	 * @return The authenticated user's details
	 */
	@GetMapping("/me")
	public UserDto me(Authentication authentication) {
		AppUserPrincipal principal = (AppUserPrincipal) authentication.getPrincipal();
		User user = principal.getUser();
		return toUserDto(user.getId());
	}

	/**
	 * Builds a {@link UserDto} for the given user ID.
	 *
	 * @param id The user ID
	 * @return The populated user DTO
	 */
	private UserDto toUserDto(Long id) {
		User user = userService.getById(id);
		List<Project> userProjects = projectService.getProjectsForUser(id);
		List<Task> userTasks = taskService.getTasksForUser(id);
		
		return new UserDto(id,
				user.getName(),
				user.getEmail(),
				toRoleDto(user.getRole()),
				user.getCreatedAt().toString(),
				toProjectDtoList(userProjects),
				toTaskDtoList(userTasks));
	}

	/**
	 * Converts a {@link Role} entity into a {@link RoleDto}.
	 *
	 * @param role The role entity
	 * @return The role DTO
	 */
	private RoleDto toRoleDto(Role role) {
		return new RoleDto(role.getId(),
				role.getName());
	}

	/**
	 * Converts a list of {@link Project} entities into DTOs.
	 *
	 * @param projects The list of projects
	 * @return The list of project DTOs
	 */
	private List<ProjectResponseDto> toProjectDtoList(List<Project> projects) {
		List<ProjectResponseDto> projectResponseDtos = new ArrayList<>();
		
		for (Project project : projects) {
			ProjectResponseDto dto = new ProjectResponseDto();
			dto.id = project.getId();
			dto.title = project.getTitle();
			dto.description = project.getDescription();
			dto.createdAt = project.getCreatedAt() != null ? project.getCreatedAt().toString() : null;
			if (project.getCreatedBy() != null) {
				dto.createdById = project.getCreatedBy().getId();
				dto.createdByName = project.getCreatedBy().getName();
				dto.isOwner = project.getCreatedBy().getId().equals(1L);
			}
			projectResponseDtos.add(dto);
		}
		
		return projectResponseDtos;
	}

	/**
	 * Converts a list of {@link Task} entities into DTOs.
	 *
	 * @param tasks The list of tasks
	 * @return The list of task DTOs
	 */
	private List<TaskResponseDto> toTaskDtoList(List<Task> tasks) {
		List<TaskResponseDto> taskResponseDtos = new ArrayList<>();
		
		for (Task task : tasks) {
			TaskResponseDto dto = new TaskResponseDto();
			dto.id = task.getId();
			dto.title = task.getTitle();
			dto.description = task.getDescription();
			dto.deadline = task.getDeadline() != null ? task.getDeadline().toString() : null;
			dto.projectId = task.getProject() != null ? task.getProject().getId() : null;
			
			Status currentStatus = taskService.getCurrentStatus(task.getId());
			dto.status = currentStatus != null ? currentStatus.getName() : "Unknown";
			
			dto.createdByName = null;
			taskResponseDtos.add(dto);
		}
		
		return taskResponseDtos;
	}
}
