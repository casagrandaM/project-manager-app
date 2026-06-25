package at.jku.app.service;

import at.jku.app.entity.Role;
import at.jku.app.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for managing {@link Role} entities.
 */
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves all available roles.
     *
     * @return The list of all roles
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Retrieves a role by its ID.
     *
     * @param id The role ID
     * @return The matching role
     *
     * @throws RuntimeException If no role with the given ID exists
     */
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    /**
     * Creates and persists a new role.
     *
     * @param role The role to create
     * @return The created role
     */
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
}
