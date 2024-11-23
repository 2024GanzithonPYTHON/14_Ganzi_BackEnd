package likelion.prolink.controller;


import com.sun.jdi.request.DuplicateRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.UserProject;
import likelion.prolink.domain.dto.request.CheckRequest;
import likelion.prolink.domain.dto.request.ProjectRequest;
import likelion.prolink.domain.dto.request.RegisterRequest;
import likelion.prolink.domain.dto.response.*;
import likelion.prolink.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api")
@Tag(name = "프로젝트 관련 API")
public class ProjectController {
    private final ProjectService projectService;

    //프로젝트 생성
    @PostMapping("/project")
    @Operation(summary = "프로젝트 생성 API")
    public ResponseEntity<?> createProject(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody ProjectRequest projectRequest){
        try {
            ProjectResponse projectResponse = projectService.createProject(customUserDetails, projectRequest);
            return ResponseEntity.ok(projectResponse);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // 전체 프로젝트 조회
    @GetMapping("/all/project")
    @Operation(summary = "전체 프로젝트 조회 API - 토큰 X")
    public ResponseEntity<?> getAllProject(){
        try {
            List<ProjectAllResponse> projectList = projectService.getAll();
            return ResponseEntity.ok(projectList);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 카테고리별 프로젝트 조회
    @GetMapping("/all/project/category")
    @Operation(summary = "카테고리별 프로젝트 조회 API - 토큰 X, category = ? 형태로 값을 줘야함")
    public ResponseEntity<?> getCatProject(@RequestParam(name = "category") String cate){
        try {
            List<ProjectAllResponse> projectList = projectService.getCatProject(cate);
            return ResponseEntity.ok(projectList);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //프로젝트 상세 조회
    @GetMapping("/project/{projectId}")
    @Operation(summary = "프로젝트 상세 조회 API")
    public ResponseEntity<?> getProject(@PathVariable Long projectId,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try {
            ProjectResponse project = projectService.getProject(customUserDetails, projectId);
            return ResponseEntity.ok(project);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    //프로젝트 삭제
    @DeleteMapping("/project/{projectId}")
    @Operation(summary = "프로젝트 삭제 API")
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try {
            projectService.deleteProject(customUserDetails, projectId);
            return ResponseEntity.ok("해당 프로젝트가 삭제되었습니다.");
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //프로젝트 참가 신청
    @PostMapping("/project/{projectId}/apply")
    @Operation(summary = "프로젝트 참가 신청 API")
    public ResponseEntity<?> registerProject(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @RequestBody RegisterRequest registerRequest,
                                             @PathVariable Long projectId){
        try {
            UserProjectResponse userProjectResponse = projectService.registerProject(customUserDetails, registerRequest, projectId);
            return ResponseEntity.ok(userProjectResponse);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (DuplicateRequestException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 프로젝트에 속해있습니다.");
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }
    //프로젝트 신청 리스트 + 프로젝트 간단 소개
    @GetMapping("/project/{projectId}/manage")
    @Operation(summary = "프로젝트 신청 리스트 및 프로젝트 간단 소개 API - 멤버 관리")
    public ResponseEntity<?> getManagePage(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @PathVariable Long projectId){
        try {
            MemberManageResponse userProjectResponse = projectService.getManage(customUserDetails, projectId);
            return ResponseEntity.ok(userProjectResponse);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    //프로젝트 멤버 신청 수락
    @PutMapping("/project/{projectId}/{nickName}/accept")
    @Operation(summary = "프로젝트 신청 수락 API - 멤버 관리")
    public ResponseEntity<?> acceptMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @PathVariable String nickName,
                                          @PathVariable Long projectId){
        try {
            String decodedNic = URLDecoder.decode(nickName, StandardCharsets.UTF_8);
            UserProjectResponse userProjectResponse = projectService.acceptMember(customUserDetails, decodedNic, projectId);
            return ResponseEntity.ok(userProjectResponse);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    //프로젝트 멤버 거절 및 추방
    @DeleteMapping("/project/{projectId}/{nickName}/delete")
    @Operation(summary = "프로젝트 신청 거절/팀원 추방 API - 멤버 관리")
    public ResponseEntity<?>  deleteMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @RequestParam(name = "nickName") String nickName,
                                          @PathVariable Long projectId){
        try {
            projectService.deleteMember(customUserDetails, nickName, projectId);
            return ResponseEntity.ok("삭제되었습니다.");
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    //다른 사람이 참여중인 프로젝트 조회
    @GetMapping("/project/{nickName}/active")
    @Operation(summary = "다른 사람이 참여중인 프로젝트 조회 API - 프로젝트 관리")
    public ResponseEntity<?> getOtherActive(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable String nickName){
        try {
            List<ActiveProjectResponse> activeProjectResponses = projectService.getUserActiveProject(customUserDetails, nickName);
            return ResponseEntity.ok(activeProjectResponses);
        }catch(NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    //내가 참가중인 프로젝트 조회
    @GetMapping("/project/active")
    @Operation(summary = "내가 참여중인 프로젝트 조회 API - 프로젝트 관리")
    public ResponseEntity<?> getUserActive(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        try {
            String nickName = customUserDetails.getUser().getNickName();
            List<ActiveProjectResponse> activeProjectResponses = projectService.getUserActiveProject(customUserDetails, nickName);
            return ResponseEntity.ok(activeProjectResponses);
        }catch(NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //다른 사람이 참여했던 프로젝트 조회
    @GetMapping("/project/{nickName}/end")
    @Operation(summary = "다른 사람이 참여했던 프로젝트 조회 API - 프로젝트 관리")
    public ResponseEntity<?> getOtherNotActive(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                           @PathVariable String nickName){
        try {
            List<ActiveProjectResponse> activeProjectResponses = projectService.getUserNonActiveProject(customUserDetails, nickName);
            return ResponseEntity.ok(activeProjectResponses);
        }catch(NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    //내가 참가했던 프로젝트 조회
    @GetMapping("/project/end")
    @Operation(summary = "내가 참가했던 프로젝트 조회 API - 프로젝트 관리")
    public ResponseEntity<?> getUserNotActive(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        try {
            String nickName = customUserDetails.getUser().getNickName();
            List<ActiveProjectResponse> activeProjectResponses = projectService.getUserNonActiveProject(customUserDetails, nickName);
            return ResponseEntity.ok(activeProjectResponses);
        }catch(NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //팀원만 조회
    @GetMapping("/project/{projectId}/member")
    @Operation(summary = "프로젝트 참여중인 팀원만 조회 API - 멤버 관리")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                       @PathVariable Long projectId){
        try {
            MemberManageResponse userProjectResponse = projectService.getMember(customUserDetails, projectId);
            return ResponseEntity.ok(userProjectResponse);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/project/{projectId}/{nickName}/owner")
    @Operation(summary = "팀장 변경 API - 멤버 관리")
    public ResponseEntity<?> changeLeader(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                         @PathVariable Long projectId,
                                         @PathVariable String nickName){
        try {
            UserProjectResponse userProjectResponse = projectService.changeLeader(customUserDetails, projectId, nickName);
            return ResponseEntity.ok(userProjectResponse);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/project/{projectId}/success")
    @Operation(summary = "프로젝트 완성 API")
    public ResponseEntity<?> successProject(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                            @PathVariable Long projectId,
                                            @RequestBody CheckRequest checkRequest){
        try {
            ProjectResponse projectResponse = projectService.successProject(customUserDetails, projectId, checkRequest);
            return ResponseEntity.ok(projectResponse);
        }catch (NotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

}
