package likelion.prolink.controller;


import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.Project;
import likelion.prolink.domain.dto.request.MeetingRequest;
import likelion.prolink.domain.dto.request.ProjectRequest;
import likelion.prolink.domain.dto.request.RegisterRequest;
import likelion.prolink.domain.dto.response.*;
import likelion.prolink.service.MeetingService;
import likelion.prolink.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@CrossOrigin("*")
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;
    private final MeetingService meetingService;

    @PostMapping()
    public ResponseEntity<?> createProject(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody ProjectRequest projectRequest) {
        try {
            ProjectResponse projectResponse = projectService.createProject(customUserDetails, projectRequest);
            return ResponseEntity.ok(projectResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllProject() {
        try {
            List<ProjectAllResponse> projectList = projectService.getAll();
            return ResponseEntity.ok(projectList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/category")
    public ResponseEntity<?> getCatProject(@RequestParam(name = "category") String cate) {
        try {
            List<ProjectAllResponse> projectList = projectService.getCatProject(cate);
            return ResponseEntity.ok(projectList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getCatProjecct(@PathVariable Long projectId,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            ProjectResponse project = projectService.getProject(customUserDetails, projectId);
            return ResponseEntity.ok(project);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{projectId}/apply")
    public ResponseEntity<?> registerProject(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                             @RequestBody RegisterRequest registerRequest,
                                             @PathVariable Long projectId) {
        try {
            UserProjectResponse userProjectResponse = projectService.registerProject(customUserDetails, registerRequest, projectId);
            return ResponseEntity.ok(userProjectResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @GetMapping("/{projectId}/member")
    public ResponseEntity<?> getMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                       @PathVariable Long projectId) {
        try {
            List<UserProjectResponse> userProjectResponse = projectService.getMember(customUserDetails, projectId);
            return ResponseEntity.ok(userProjectResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }

    @PutMapping("/{projectId}/accept")
    public ResponseEntity<?> acceptMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @RequestParam(name = "nickName") String nickName,
                                          @PathVariable Long projectId) {
        try {
            UserProjectResponse userProjectResponse = projectService.acceptMember(customUserDetails, nickName, projectId);
            return ResponseEntity.ok(userProjectResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{projectId}/accept")
    public ResponseEntity<?> deleteMember(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                          @RequestParam(name = "nickName") String nickName,
                                          @PathVariable Long projectId) {
        try {
            projectService.deleteMember(customUserDetails, nickName, projectId);
            return ResponseEntity.ok("삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/{projectId}/meeting")
    public ResponseEntity<?> summarizeMeeting(@RequestBody MeetingRequest meetingRequest,
                                              @PathVariable ("projectId") Long projectId) {
        try {
            Project project = projectService.findById(projectId);
            MeetingResponse meetingResponse = meetingService.summarizeContent(meetingRequest, project);
            return ResponseEntity.ok(meetingResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{projectId}/meeting")
    public ResponseEntity<List<MeetingResponse>> getAllMeetings(@PathVariable("projectId") Long projectId) {
        try {
            List<MeetingResponse> meetings = meetingService.getAllMeetingsByProjectId(projectId);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }

    @GetMapping("/{projectId}/meeting/{meetingId}")
    public ResponseEntity<MeetingResponse> getMeetingById(
            @PathVariable("projectId") Long projectId,
            @PathVariable("meetingId") Long meetingId) {
        try {
            MeetingResponse meeting = meetingService.getMeetingByProjectAndMeetingId(projectId, meetingId);

            if (meeting != null) {
                return ResponseEntity.ok(meeting);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}
