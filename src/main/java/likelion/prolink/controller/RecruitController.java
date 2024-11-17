package likelion.prolink.controller;

import likelion.prolink.dto.ApplyRequestDto;
import likelion.prolink.dto.ApplyResponseDto;
import likelion.prolink.dto.RecruitDto;
import likelion.prolink.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recruit")
public class RecruitController {

    private final RecruitService recruitService;

    @GetMapping
    public List<RecruitDto> getRecruitProjects() {
        return recruitService.getAllProjects();
    }

    @PostMapping("/{projectId}/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public ApplyResponseDto applyForProject(
            @PathVariable Long projectId,
            @RequestBody ApplyRequestDto applyRequestDto,
            @RequestHeader("Authorization") String token) {

        return recruitService.applyForProject(projectId, applyRequestDto);
    }
}
