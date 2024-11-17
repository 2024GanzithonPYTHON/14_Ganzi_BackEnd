package likelion.prolink.service;

import likelion.prolink.dto.ProjectResponseDto;
import likelion.prolink.dto.RecruitDto;
import likelion.prolink.domain.Project;
import likelion.prolink.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final ProjectRepository projectRepository;

    public List<RecruitDto> getAllProjects() {
        List<Project> projects = projectRepository.findAll();

        return projects.stream()
                .map(project -> new RecruitDto(
                        project.getId(),
                        project.getProjectName(),
                        project.getTitle(),
                        project.getContributorNum(),
                        project.getIsRecruit(),
                        project.getIsActive(),
                        project.getCategory(),
                        project.getEnd()
                        //project.getNickName()
                ))
                .collect(Collectors.toList());
    }
}
