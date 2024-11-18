package likelion.prolink.service;

import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.Project;
import likelion.prolink.domain.User;
import likelion.prolink.domain.UserProject;
import likelion.prolink.domain.dto.request.ProjectRequest;
import likelion.prolink.domain.dto.request.RegisterRequest;
import likelion.prolink.domain.dto.response.ProjectAllResponse;
import likelion.prolink.domain.dto.response.ProjectResponse;
import likelion.prolink.domain.dto.response.UserProjectResponse;
import likelion.prolink.repository.ProjectRepository;
import likelion.prolink.repository.UserProjectRepository;
import likelion.prolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GlobalSevice globalSevice;
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;

    public ProjectResponse createProject(CustomUserDetails customUserDetails, ProjectRequest projectRequest){
        User user = globalSevice.findUser(customUserDetails);

        Project project = new Project();
        project.setProjectName(projectRequest.getProjectName());
        project.setContent(projectRequest.getContent());
        project.setGrade(projectRequest.getGrade());
        project.setTitle(projectRequest.getTitle());
        project.setContributorNum(projectRequest.getContributorNum());
        project.setUser(user);
        project.setDeadLine(projectRequest.getDeadLine());
        project.setRecruitmentPosition(projectRequest.getRecruitmentPosition());
        project.setCategory(projectRequest.getCategory());
        project.setIsActive(true);
        project.setIsRecruit(true);

        projectRepository.save(project);

        user.setPoint(user.getPoint() - 5);
        userRepository.save(user);

        UserProject userProject = new UserProject();
        userProject.setUser(user);
        userProject.setProject(project);
        userProject.setAuthority("팀장");
        userProject.setContent(null);
        userProject.setCareerUrl(null);
        userProject.setIsAccepted(true);
        userProjectRepository.save(userProject);

        return projectResponse(project);
    }

    public List<ProjectAllResponse> getAll(){

        List<ProjectAllResponse> projectAllResponses = projectRepository.findAllByIsRecruit(true).stream().map(project -> new ProjectAllResponse(
                project.getId(), project.getUser().getNickName(), project.getProjectName(), project.getTitle(),
                project.getIsRecruit(), project.getContributorNum(), getNum(project.getId()), project.getCategory(), project.getRecruitmentPosition(),
                project.getContent(), project.getDeadLine())).collect(Collectors.toList());

        return projectAllResponses;
    }

    public List<ProjectAllResponse> getCatProject(String category){
        List<ProjectAllResponse> projectAllResponses = projectRepository.findAllByIsRecruitAndCategory(true, category).stream().map(project -> new ProjectAllResponse(
                project.getId(), project.getUser().getNickName(), project.getProjectName(), project.getTitle(),
                project.getIsRecruit(), project.getContributorNum(), getNum(project.getId()), project.getCategory(), project.getRecruitmentPosition(),
                project.getContent(), project.getDeadLine())).collect(Collectors.toList());

        return projectAllResponses;
    }

    public ProjectResponse getProject(CustomUserDetails customUserDetails, Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        globalSevice.findUser(customUserDetails);

        return projectResponse(project);
    }

    public UserProjectResponse registerProject(CustomUserDetails customUserDetails, RegisterRequest registerRequest, Long projectId ){
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        UserProject userProject = new UserProject();
        userProject.setUser(user); userProject.setProject(project);
        userProject.setAuthority("팀원"); userProject.setCareerUrl(registerRequest.getCareerUrl());
        userProject.setContent(registerRequest.getContent());
        userProject.setIsAccepted(false);

        userProjectRepository.save(userProject);

        return new UserProjectResponse(userProject.getUser().getNickName(),userProject.getProject().getId(),
                userProject.getAuthority(), userProject.getCareerUrl(),
                userProject.getContent(), userProject.getIsAccepted());
    }

    public List<UserProjectResponse> getMember(CustomUserDetails customUserDetails, Long projectId){
        globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));
        List<UserProjectResponse> userProjectResponses = userProjectRepository.findByProject(project)
                .stream().map(userProject -> new UserProjectResponse(userProject.getUser().getNickName(),userProject.getProject().getId(),
                        userProject.getAuthority(), userProject.getCareerUrl(),
                        userProject.getContent(), userProject.getIsAccepted())).collect(Collectors.toList());

        return userProjectResponses;
    }

    @Transactional
    public UserProjectResponse acceptMember(CustomUserDetails customUserDetails, String nickName, Long projectId){
        User user = globalSevice.findUser(customUserDetails);

        UserProject userProject = userProjectRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        if(userProject.getAuthority().equals("팀원")){
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        User member = userRepository.findByNickName(nickName)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        UserProject accept = userProjectRepository.findByUser(member)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));

        accept.setIsAccepted(true);
        member.setPoint(member.getPoint() - 5);
        userProjectRepository.save(accept);

        return new UserProjectResponse(userProject.getUser().getNickName(),userProject.getProject().getId(),
                userProject.getAuthority(), userProject.getCareerUrl(),
                userProject.getContent(), userProject.getIsAccepted());
    }

    @Transactional
    public void deleteMember(CustomUserDetails customUserDetails, String nickName, Long projectId){
        User user = globalSevice.findUser(customUserDetails);

        UserProject userProject = userProjectRepository.findByUser(user)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        if(userProject.getAuthority().equals("팀원")){
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        User member = userRepository.findByNickName(nickName)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        UserProject accept = userProjectRepository.findByUser(member)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));

        userProjectRepository.delete(accept);
    }


    // 현재 참가한 참여자 수 반환
    public Long getNum(Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        Long count = userProjectRepository.countByProjectAndIsAcceptedTrue(project);
        return count;
    }

    // 세부 프로젝트 내용 반환
    public ProjectResponse projectResponse(Project project){
        ProjectResponse projectResponse = new ProjectResponse(
                project.getId(),
                project.getUser().getNickName(),
                project.getUser().getId(),
                project.getProjectName(),
                project.getTitle(),
                project.getGrade(),
                project.getIsRecruit(),
                project.getIsActive(),
                project.getContributorNum(),
                project.getCategory(),
                project.getRecruitmentPosition(),
                project.getContent(),
                project.getDeadLine(),
                project.getLink()
        );

        return projectResponse;
    }
}
