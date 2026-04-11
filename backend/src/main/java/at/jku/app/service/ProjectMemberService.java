package at.jku.app.service;

import at.jku.app.entity.ProjectMember;
import at.jku.app.repository.ProjectMemberRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository) {
        this.projectMemberRepository = projectMemberRepository;
    }

    public List<ProjectMember> getAll() {
        return projectMemberRepository.findAll();
    }

    public List<ProjectMember> getByProjectId(Long projectId) {
        return projectMemberRepository.findByProjectId(projectId);
    }

    public ProjectMember addMember(ProjectMember member) {
        return projectMemberRepository.save(member);
    }

    public void removeMember(Long id) {
        projectMemberRepository.deleteById(id);
    }
}