package at.jku.app.controller;

import at.jku.app.dto.*;
import at.jku.app.entity.*;
import at.jku.app.service.ProjectService;
import at.jku.app.service.TaskService;
import at.jku.app.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
	
	private static final Long CURRENT_USER_ID = 1L;
	
	private final UserService userService;
	private final ProjectService projectService;
	private final TaskService taskService;
	
	public UserController(UserService userService, ProjectService projectService, TaskService taskService) {
		this.userService = userService;
		this.projectService = projectService;
		this.taskService = taskService;
	}
	
	@GetMapping
	public List<UserDto> getAllUsers() {
		List<User> users = userService.getAllUsers();
		List<UserDto> userDtos = new ArrayList<>();
		
		for (User user : users) {
			userDtos.add(toUserDto(user.getId()));
		}
		
		return userDtos;
	}
	
	@GetMapping("/{id}")
	public UserDto getUser(@PathVariable Long id) {
		return toUserDto(id);
	}
	
	@PutMapping("/{id}")
	public UserDto updateUser(@PathVariable Long id, @RequestBody UserUpdateDto newData) {
		userService.updateUser(id, newData.name(), newData.email());
		return toUserDto(id);
	}
	
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
	
	private RoleDto toRoleDto(Role role) {
		return new RoleDto(role.getId(),
				role.getName());
	}
	
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
				dto.isOwner = project.getCreatedBy().getId().equals(CURRENT_USER_ID);
			}
			projectResponseDtos.add(dto);
		}
		
		return projectResponseDtos;
	}
	
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
