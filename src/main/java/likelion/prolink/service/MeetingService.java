package likelion.prolink.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import likelion.prolink.domain.CustomUserDetails;
import likelion.prolink.domain.Meeting;
import likelion.prolink.domain.Project;
import likelion.prolink.domain.User;
import likelion.prolink.domain.dto.request.ChatRequest;
import likelion.prolink.domain.dto.request.MeetingRequest;
import likelion.prolink.domain.dto.response.ChatResponse;
import likelion.prolink.domain.dto.response.MeetingResponse;
import likelion.prolink.repository.MeetingRepository;
import likelion.prolink.repository.ProjectRepository;
import likelion.prolink.repository.UserProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeetingService {
    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.system}")
    private String roleMessage;

    private final GlobalSevice globalSevice;
    private final MeetingRepository meetingRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;

    private static final String GPT_API_URL = "https://api.openai.com/v1/chat/completions";


    public MeetingResponse createContent(CustomUserDetails customUserDetails, MeetingRequest meetingRequest, Long projectId) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));
        String requestBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"system\", \"content\": \"" + roleMessage + "\"},"
                + "               {\"role\": \"user\", \"content\": \"" + meetingRequest.getTitle() + " " + meetingRequest.getContent() + "\"}],"
                + "\"max_tokens\": 1500"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // HTTP 요청 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // RestTemplate을 사용하여 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(GPT_API_URL, HttpMethod.POST, entity, String.class);

        Meeting meeting = new Meeting();
        meeting.setUser(user);
        meeting.setProject(project);
        meeting.setTitle(meetingRequest.getTitle());
        meeting.setMeetingDate(LocalDate.now());
        meeting.setContent(meetingRequest.getContent());
        meeting.setGptComment(extractGptMessageContent(response.getBody()));

        meeting = meetingRepository.save(meeting);

        // 응답 처리
        return new MeetingResponse(
                meeting.getId(),
                user.getId(),
                project.getId(),
                meeting.getTitle(),
                meeting.getContent(),
                meeting.getGptComment(),
                meeting.getMeetingDate()
        );
    }

    public ChatResponse summarizeContent(CustomUserDetails customUserDetails, ChatRequest chatRequest, Long projectId) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        String requestBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"system\", \"content\": \"" + roleMessage + "\"},"
                + "               {\"role\": \"user\", \"content\": \"" + chatRequest.getContent() + "\"}],"
                + "\"max_tokens\": 1500"
                + "}";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        // HTTP 요청 엔티티 생성
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // RestTemplate을 사용하여 POST 요청
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(GPT_API_URL, HttpMethod.POST, entity, String.class);

        return new ChatResponse(
        user.getId(), project.getId(), chatRequest.getContent(),
        extractGptMessageContent(response.getBody()), LocalDateTime.now());

    }
    private String extractGptMessageContent(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode messageContent = rootNode.path("choices").get(0).path("message").path("content");
            return messageContent.asText();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to parse GPT response", e);
        }
    }

    public List<MeetingResponse> getMeetingsByProjectId(Long projectId, CustomUserDetails customUserDetails) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        List<Meeting> meetings = meetingRepository.findAllByProjectId(projectId);

        return meetings.stream().map(meeting -> new MeetingResponse(
                meeting.getId(),
                meeting.getUser().getId(),
                meeting.getProject().getId(),
                meeting.getTitle(),
                meeting.getContent(),
                meeting.getGptComment(),
                meeting.getMeetingDate()
        )).collect(Collectors.toList());
    }

    public MeetingResponse getMeeting(Long projectId, Long meetingId, CustomUserDetails customUserDetails) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        Meeting meeting = meetingRepository.findById(meetingId)
                .orElseThrow(()-> new NotFoundException("해당 회의록은 존재하지 않습니다"));

        return new MeetingResponse(
                meeting.getId(),
                meeting.getUser().getId(),
                meeting.getProject().getId(),
                meeting.getTitle(),
                meeting.getContent(),
                meeting.getGptComment(),
                meeting.getMeetingDate()
        );
    }
}
