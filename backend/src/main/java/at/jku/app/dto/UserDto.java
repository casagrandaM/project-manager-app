package at.jku.app.dto;

import java.util.List;

/**
 * Data transfer object representing a user and their related data.
 *
 * @param id        The user ID
 * @param name      The username
 * @param email     The user email address
 * @param role      The user role
 * @param createdAt The creation timestamp
 * @param projects  The projects assigned to the user
 * @param tasks     The tasks assigned to the user
 */
public record UserDto(Long id,
                      String name,
                      String email,
                      RoleDto role,
                      String createdAt,
                      List<ProjectResponseDto> projects,
					  List<TaskResponseDto> tasks) {}
