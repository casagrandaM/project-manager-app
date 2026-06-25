package at.jku.app.controller;

import at.jku.app.dto.*;
import at.jku.app.entity.*;
import at.jku.app.service.ProjectService;
import at.jku.app.service.TaskService;
import at.jku.app.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * REST controller for managing users and exposing user-related data.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
	
	private final UserService userService;
	private final ProjectService projectService;
	private final TaskService taskService;
	
	public UserController(UserService userService, ProjectService projectService, TaskService taskService) {
		this.userService = userService;
		this.projectService = projectService;
		this.taskService = taskService;
	}

	/**
	 * Retrieves all users with their associated projects and tasks.
	 *
	 * @return The list of users as {@link UserDto}s
	 */
	@GetMapping
	public List<UserDto> getAllUsers() {
		List<User> users = userService.getAllUsers();
		List<UserDto> userDtos = new ArrayList<>();
		
		for (User user : users) {
			userDtos.add(toUserDto(user.getId()));
		}
		
		return userDtos;
	}

	/**
	 * Retrieves a user by ID.
	 *
	 * @param id The user ID
	 * @return The user as {@link UserDto}
	 */
	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable Long id) {
		return toUserDto(id);
	}

	/**
	 * Updates the name and email of a user.
	 *
	 * @param id      The user ID
	 * @param newData The updated user data
	 * @return The updated user as {@link UserDto}
	 */
	@PutMapping("/{id}")
	public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto newData) {
		userService.updateUser(id, newData.name(), newData.email());
		return toUserDto(id);
	}

	/**
	 * Creates a {@link UserDto} including projects, tasks, and role information.
	 *
	 * @param id The user ID
	 * @return The populated {@link UserDto}
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
	 * Converts a role entity to a {@link RoleDto}.
	 *
	 * @param role The role entity
	 * @return The {@link RoleDto}
	 */
	private RoleDto toRoleDto(Role role) {
		return new RoleDto(role.getId(),
				role.getName());
	}

	/**
	 * Converts a list of projects to {@link ProjectResponseDto}s.
	 *
	 * @param projects The projects to convert
	 * @return The list of projects as {@link ProjectResponseDto}s
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
				dto.isOwner = project.getCreatedBy().getId().equals(userService.getCurrentUser().getId());
			}
			projectResponseDtos.add(dto);
		}
		
		return projectResponseDtos;
	}

	/**
	 * Converts a list of tasks to {@link TaskResponseDto}s.
	 *
	 * @param tasks The tasks to convert
	 * @return The list of tasks as {@link TaskResponseDto}s
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
