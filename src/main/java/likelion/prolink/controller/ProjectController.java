package likelion.prolink.controller;

import likelion.prolink.dto.ProjectRequestDto;
import likelion.prolink.dto.ProjectResponseDto;
import likelion.prolink.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/project")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/create")
    public ResponseEntity<ProjectResponseDto> createProject(@RequestBody ProjectRequestDto requestDto) {
        ProjectResponseDto responseDto = projectService.createProject(requestDto);
        return ResponseEntity.status(201).body(responseDto);
    }
}
