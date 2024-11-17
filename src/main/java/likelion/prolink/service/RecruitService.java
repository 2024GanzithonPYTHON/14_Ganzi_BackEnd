package likelion.prolink.service;

import likelion.prolink.domain.UserProject;
import likelion.prolink.dto.ApplyRequestDto;
import likelion.prolink.dto.ApplyResponseDto;
import likelion.prolink.dto.RecruitDto;
import likelion.prolink.domain.Project;
import likelion.prolink.repository.ProjectRepository;
import likelion.prolink.repository.UserProjectRepository;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitService {

    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;

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

    public ApplyResponseDto applyForProject(Long projectId, ApplyRequestDto applyRequestDto) {

        UserProject userProject = new UserProject();
        userProject.setProjectId(projectId);
        userProject.setCareerUrl(applyRequestDto.getCareerUrl());
        userProject.setContent(applyRequestDto.getContent());
        userProject.setJoinDate(LocalDateTime.now());

        UserProject savedUserProject = userProjectRepository.save(userProject);

        return new ApplyResponseDto(
                savedUserProject.getId(),
                savedUserProject.getCareerUrl(),
                savedUserProject.getContent()
        );
    }

}
