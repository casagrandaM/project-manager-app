package at.jku.app.dto;

import java.util.List;

public record UserDto(Long id,
                      String name,
                      String email,
                      RoleDto role,
                      String createdAt,
                      List<ProjectResponseDto> projects,
					  List<TaskResponseDto> tasks) {}
