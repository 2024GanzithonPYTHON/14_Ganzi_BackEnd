package likelion.prolink.service;

import com.sun.jdi.request.DuplicateRequestException;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.Project;
import likelion.prolink.domain.User;
import likelion.prolink.domain.UserProject;
import likelion.prolink.domain.dto.request.CheckRequest;
import likelion.prolink.domain.dto.request.ProjectRequest;
import likelion.prolink.domain.dto.request.RegisterRequest;
import likelion.prolink.domain.dto.response.*;
import likelion.prolink.repository.ProjectRepository;
import likelion.prolink.repository.UserProjectRepository;
import likelion.prolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final GlobalSevice globalSevice;
    private final UserProjectRepository userProjectRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse createProject(CustomUserDetails customUserDetails, ProjectRequest projectRequest){
        User user = globalSevice.findUser(customUserDetails);
        if(user.getPoint() < 5){
            throw new RuntimeException("포인트가 부족합니다.");
        }

        Project project = new Project();
        project.setProjectName(projectRequest.getProjectName());
        project.setContent(projectRequest.getContent());
        project.setGrade(projectRequest.getGrade());
        project.setTitle(projectRequest.getTitle());
        project.setContributorNum(projectRequest.getContributorNum());
        project.setUser(user);
        project.setLink(projectRequest.getLink());
        project.setDeadLine(projectRequest.getDeadLine());
        project.setRecruitmentPosition(projectRequest.getRecruitmentPosition());
        project.setCategory(projectRequest.getCategory());
        project.setIsActive(true);
        project.setIsRecruit(true);
        project.setSuccessLink(null);

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

    @Transactional
    public void deleteProject(CustomUserDetails customUserDetails, Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        User user = globalSevice.findUser(customUserDetails);

        if(project.getUser().getId() != user.getId()){
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        if(userProjectRepository.countByProjectAndIsAcceptedTrue(project) > 1){
            throw new RuntimeException("이미 프로젝트에 참가한 유저가 있습니다.");
        }

        projectRepository.delete(project);
    }

    @Transactional
    public UserProjectResponse registerProject(CustomUserDetails customUserDetails, RegisterRequest registerRequest, Long projectId ){
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        if(userProjectRepository.findByUserAndProject(user,project).isPresent()){
            throw new DuplicateRequestException("이미 프로젝트에 속해있습니다.");
        }

        if(project.getIsRecruit() == false){
            throw new RuntimeException("이미 모집 마감되었습니다.");
        }

        if(user.getPoint() < 5){
            throw new RuntimeException("포인트가 부족합니다.");
        }

        UserProject userProject = new UserProject();
        userProject.setUser(user); userProject.setProject(project);
        userProject.setAuthority("팀원"); userProject.setCareerUrl(registerRequest.getCareerUrl());
        userProject.setContent(registerRequest.getContent());
        userProject.setIsAccepted(false);

        UserProject save = userProjectRepository.save(userProject);

        return new UserProjectResponse(save.getUser().getNickName(),save.getProject().getId(),
                save.getAuthority(), save.getCareerUrl(),
                save.getContent(), save.getIsAccepted());
    }

    public MemberManageResponse getManage(CustomUserDetails customUserDetails, Long projectId){
        globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));
        List<UserProjectResponse> userProjectResponses = userProjectRepository.findByProject(project)
                .stream().map(userProject -> new UserProjectResponse(userProject.getUser().getNickName(),userProject.getProject().getId(),
                        userProject.getAuthority(), userProject.getCareerUrl(),
                        userProject.getContent(), userProject.getIsAccepted())).collect(Collectors.toList());

        return new MemberManageResponse(userProjectResponses,project.getProjectName(), project.getTitle(), project.getUser().getNickName());
    }

    @Transactional
    public UserProjectResponse acceptMember(CustomUserDetails customUserDetails, String nickName, Long projectId){
        User user = globalSevice.findUser(customUserDetails);


        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        UserProject userProject = userProjectRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));


        if(userProject.getAuthority().equals("팀원")){
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        User member = userRepository.findByNickName(nickName)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        UserProject accept = userProjectRepository.findByUserAndProject(member,project)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));

        accept.setIsAccepted(true);
        member.setPoint(member.getPoint() - 5);
        userProjectRepository.save(accept);
        userRepository.save(member);

        if (getNum(projectId) == project.getContributorNum()){
            project.setIsRecruit(false);
            projectRepository.save(project);
        }

        return new UserProjectResponse(userProject.getUser().getNickName(),userProject.getProject().getId(),
                userProject.getAuthority(), userProject.getCareerUrl(),
                userProject.getContent(), userProject.getIsAccepted());
    }

    @Transactional
    public void deleteMember(CustomUserDetails customUserDetails, String nickName, Long projectId){
        User user = globalSevice.findUser(customUserDetails);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        UserProject userProject = userProjectRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));

        if(userProject.getAuthority().equals("팀원")){
            throw new IllegalArgumentException("수정 권한이 없습니다");
        }

        if(userProject.getAuthority().equals("팀장") && user.getNickName().equals(nickName)){
            throw new IllegalArgumentException("팀장은 나갈 수 없습니다. 다른 팀장을 지정해주세요.");
        }

        User member = userRepository.findByNickName(nickName)
                .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다."));

        UserProject accept = userProjectRepository.findByUserAndProject(member,project)
                .orElseThrow(() -> new NotFoundException("해당 프로젝트에 존재하지 않습니다."));

        userProjectRepository.delete(accept);

        if (getNum(projectId) < project.getContributorNum()){
            project.setIsRecruit(true);
            projectRepository.save(project);
        }
    }

    public List<ActiveProjectResponse> getUserActiveProject(CustomUserDetails customUserDetails,
                                                            String nickName){
        globalSevice.findUser(customUserDetails);
        User user = userRepository.findByNickName(nickName).orElseThrow(()->new NotFoundException("해당 유저를 찾을 수 없습니다."));
        return userProjectRepository.findByUserAndIsAcceptedTrue(user)
                .stream()
                .filter(userProject -> userProject.getProject().getIsActive())
                .map(userProject -> new ActiveProjectResponse(userProject.getProject().getId(),
                        nickName, userProject.getProject().getProjectName(), userProject.getProject().getTitle(),
                        userProject.getProject().getIsActive(),userProject.getProject().getContent()))
                .collect(Collectors.toList());
    }

    public List<ActiveProjectResponse> getUserNonActiveProject(CustomUserDetails customUserDetails,
                                                            String nickName){
        globalSevice.findUser(customUserDetails);
        User user = userRepository.findByNickName(nickName).orElseThrow(()->new NotFoundException("해당 유저를 찾을 수 없습니다."));
        return userProjectRepository.findByUserAndIsAcceptedTrue(user)
                .stream()
                .filter(userProject -> !userProject.getProject().getIsActive())
                .map(userProject -> new ActiveProjectResponse(userProject.getProject().getId(),
                        nickName, userProject.getProject().getProjectName(), userProject.getProject().getTitle(),
                        userProject.getProject().getIsActive(),userProject.getProject().getContent()))
                .collect(Collectors.toList());
    }

    public MemberManageResponse getMember(CustomUserDetails customUserDetails, Long projectId){
        globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));
        List<UserProjectResponse> userProjectResponses = userProjectRepository.findByProjectAndIsAcceptedTrue(project)
                .stream().map(userProject -> new UserProjectResponse(userProject.getUser().getNickName(),userProject.getProject().getId(),
                        userProject.getAuthority(), userProject.getCareerUrl(),
                        userProject.getContent(), userProject.getIsAccepted())).collect(Collectors.toList());

        return new MemberManageResponse(userProjectResponses,project.getProjectName(), project.getTitle(), project.getUser().getNickName());
    }
    public UserProjectResponse changeLeader(CustomUserDetails customUserDetails, Long projectId, String nickName){
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        UserProject leaderProject = userProjectRepository.findByUserAndProject(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        if(!leaderProject.getAuthority().equals("팀장")){
            throw new IllegalArgumentException("팀장 변경 권한이 없습니다.");
        }

        User member = userRepository.findByNickName(nickName)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 멤버입니다."));

        UserProject newLeaderProject = userProjectRepository.findByUserAndProject(member,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        leaderProject.setAuthority("팀원");
        newLeaderProject.setAuthority("팀장");

        userProjectRepository.save(leaderProject);
        userProjectRepository.save(newLeaderProject);

        return new UserProjectResponse(newLeaderProject.getUser().getNickName(),newLeaderProject.getProject().getId(),
                newLeaderProject.getAuthority(), newLeaderProject.getCareerUrl(),
                newLeaderProject.getContent(), newLeaderProject.getIsAccepted());
    }

    public ProjectResponse successProject(CustomUserDetails customUserDetails, Long projectId, CheckRequest checkRequest){
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트가 존재하지 않습니다"));

        UserProject userProject = userProjectRepository.findByUserAndProject(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        if(!userProject.getAuthority().equals("팀장")){
            throw new IllegalArgumentException("프로젝트 상태 변경 권한이 없습니다.");
        }

        project.setSuccessLink(checkRequest.getSentence());
        project.setIsActive(false);
        Project updatedProject = projectRepository.save(project);

        userProjectRepository.findByProject(userProject.getProject()).stream()
                .filter(up -> up.getIsAccepted())
                .forEach(up -> {
                    up.getUser().setPoint(up.getUser().getPoint() + 10);
                    userRepository.save(up.getUser());
                });

        return projectResponse(updatedProject);
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
                project.getLink(),
                project.getSuccessLink()
        );

        return projectResponse;
    }
}
