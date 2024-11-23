package likelion.prolink.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.Project;
import likelion.prolink.domain.dto.request.ChatRequest;
import likelion.prolink.domain.dto.request.MeetingRequest;
import likelion.prolink.domain.dto.response.ChatResponse;
import likelion.prolink.domain.dto.response.MeetingResponse;
import likelion.prolink.repository.ProjectRepository;
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
@RequestMapping("/api")
@Tag(name = "회의록 관련 API")
public class MeetingController {
    private final MeetingService meetingService;

    // 회의록 저장 및 요약
    @PostMapping("/project/{projectId}/meeting")
    @Operation(summary = "회의록 작성/저장/요약 API - 회의록 관리")
    public ResponseEntity<?>  createMeeting(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                  @RequestBody MeetingRequest meetingRequest,
                  @PathVariable("projectId") Long projectId) {
        try {
            MeetingResponse meetingResponse = meetingService.createContent(customUserDetails, meetingRequest, projectId);
            return ResponseEntity.ok(meetingResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    // 회의록 조회
    @GetMapping("/project/{projectId}/meeting")
    @Operation(summary = "회의록 조회 API - 회의록 관리")
    public ResponseEntity<?> getAllMeetings(@PathVariable("projectId") Long projectId,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            List<MeetingResponse> meetings = meetingService.getMeetingsByProjectId(projectId, customUserDetails);
            return ResponseEntity.ok(meetings);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //특정 회의록 조회
    @GetMapping("/{projectId}/meeting/{meetingId}")
    @Operation(summary = "특정 회의록 조회 API - 회의록 관리")
    public ResponseEntity<?> getMeetingById(
            @PathVariable("projectId") Long projectId,
            @PathVariable("meetingId") Long meetingId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try {
            MeetingResponse meeting = meetingService.getMeeting(projectId, meetingId, customUserDetails);
            return ResponseEntity.ok(meeting);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
