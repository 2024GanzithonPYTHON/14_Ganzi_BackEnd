package likelion.prolink.controller;

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
@RequestMapping("/api/project/{projectId}/task")
public class TaskController {

    private final TaskService taskService;

    // 일정 저장
    @PostMapping
    public ResponseEntity<?> createTask(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable Long projectId,
                                        @RequestBody TaskRequest taskRequest) {
        try {
            TaskResponse taskResponse = taskService.createTask(customUserDetails.getUser(), projectId, taskRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(taskResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 전체 일정 조회
    @GetMapping
    public ResponseEntity<?> getTasks(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @PathVariable Long projectId) {
        try {
            List<TaskResponse> tasks = taskService.getTasks(customUserDetails.getUser(), projectId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 일정 수정
    @PutMapping
    public ResponseEntity<?> updateTask(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @PathVariable Long projectId,
                                        @PathVariable Long taskId,
                                        @RequestBody TaskRequest taskRequest) {
        try {
            TaskResponse taskResponse = taskService.updateTask(customUserDetails.getUser(), taskId, taskRequest);
            return ResponseEntity.ok(taskResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
