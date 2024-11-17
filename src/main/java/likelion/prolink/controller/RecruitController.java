package likelion.prolink.controller;

import likelion.prolink.dto.RecruitDto;
import likelion.prolink.service.RecruitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
