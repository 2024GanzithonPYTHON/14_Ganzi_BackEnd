package likelion.prolink.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.dto.request.TaskRequest;
import likelion.prolink.domain.dto.response.TaskResponse;
import likelion.prolink.service.TaskService;
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
@Tag(name = "일정 관련 API")
public class TaskController {

    private final TaskService taskService;

    // 일정 저장
    @PostMapping("/{projectId}/task")
    @Operation(summary = "일정 저장 API - 일정 관리")
    public ResponseEntity<?> createTask(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable Long projectId,
                                        @RequestBody TaskRequest taskRequest) {
        try {
            TaskResponse taskResponse = taskService.createTask(customUserDetails, projectId, taskRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 전체 일정 조회
    @GetMapping("/{projectId}/task")
    @Operation(summary = "전체 일정 조회 API - 일정 관리")
    public ResponseEntity<?> getTasks(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @PathVariable Long projectId) {
        try {
            List<TaskResponse> tasks = taskService.getTasks(customUserDetails, projectId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 일정 수정
    @PutMapping("/{projectId}/task/{taskId}")
    @Operation(summary = "특정 일정 수정 API - 일정 관리")
    public ResponseEntity<?> updateTask(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable("projectId") Long projectId,
                                        @PathVariable("taskId") Long taskId,
                                        @RequestBody TaskRequest taskRequest) {
        try {
            TaskResponse taskResponse = taskService.updateTask(customUserDetails, projectId, taskId, taskRequest);
            return ResponseEntity.ok(taskResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{projectId}/task/{taskId}/att")
    @Operation(summary = "일정 완수 ON/OFF API - 일정 관리")
    public ResponseEntity<?> toggleTask(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable("projectId") Long projectId,
                                        @PathVariable("taskId") Long taskId) {
        try {
            TaskResponse taskResponse = taskService.toggleTask(customUserDetails, projectId, taskId);
            return ResponseEntity.ok(taskResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
