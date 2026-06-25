package at.jku.app.dto;

/**
 * Data transfer object representing a user role.
 *
 * @param id   The role ID
 * @param name The role name
 */
public record RoleDto(Long id, String name) {
}
