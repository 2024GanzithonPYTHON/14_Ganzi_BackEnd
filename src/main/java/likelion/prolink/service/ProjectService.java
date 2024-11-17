package likelion.prolink.service;

import likelion.prolink.domain.Project;
import likelion.prolink.dto.ProjectRequestDto;
import likelion.prolink.dto.ProjectResponseDto;
import likelion.prolink.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectResponseDto createProject(ProjectRequestDto requestDto) {
        Project project = new Project();
        project.setProjectName(requestDto.getProjectName());
        project.setTitle(requestDto.getTitle());
        project.setGrade(requestDto.getGrade());
        project.setIsRecruit(requestDto.getIsRecruit());
        project.setIsActive(requestDto.getIsActive());
        project.setContributorNum(requestDto.getContributorNum());
        project.setCategory(requestDto.getCategory());
        project.setContent(requestDto.getContent());
        project.setEnd(requestDto.getEnd());

        Project savedProject = projectRepository.save(project);

        return new ProjectResponseDto(
                savedProject.getId(),
                savedProject.getProjectName(),
                savedProject.getTitle(),
                savedProject.getGrade(),
                savedProject.getIsRecruit(),
                savedProject.getIsActive(),
                savedProject.getContributorNum(),
                savedProject.getCategory(),
                savedProject.getContent(),
                savedProject.getRecruitmentPosition(),
                savedProject.getEnd()
        );
    }
}
