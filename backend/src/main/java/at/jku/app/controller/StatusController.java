package at.jku.app.controller;

import at.jku.app.entity.Status;
import at.jku.app.service.StatusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for retrieving available task statuses.
 */
@RestController
@RequestMapping("/api/statuses")
@CrossOrigin(origins = "*")
public class StatusController {

    private final StatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    /**
     * Retrieves all available task statuses.
     *
     * @return The list of all {@link Status} entities
     */
    @GetMapping
    public List<Status> getAllStatuses() {
        return statusService.getAllStatuses();
    }
}