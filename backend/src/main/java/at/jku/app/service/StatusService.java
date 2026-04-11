package at.jku.app.service;

import at.jku.app.entity.Status;
import at.jku.app.repository.StatusRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<Status> getAllStatuses() {
        return statusRepository.findAll();
    }

    public Status getById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found"));
    }

    public Status getByName(String name) {
        return statusRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Status not found"));
    }

    public Status getStatusById(Long id) {
        return statusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status not found: " + id));
    }
}