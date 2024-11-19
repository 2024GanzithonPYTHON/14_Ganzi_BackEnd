package likelion.prolink.service;

import likelion.prolink.domain.User;
import likelion.prolink.domain.Project;
import likelion.prolink.domain.Task;
import likelion.prolink.domain.UserProject;
import likelion.prolink.domain.dto.request.TaskRequest;
import likelion.prolink.domain.dto.response.TaskResponse;
import likelion.prolink.repository.ProjectRepository;
import likelion.prolink.repository.TaskRepository;
import likelion.prolink.repository.UserProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;

    // 일정 저장
    public TaskResponse createTask(User user, Long projectId, TaskRequest taskRequest) {
        // 프로젝트가 존재하는지 확인
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("해당 프로젝트가 존재하지 않습니다"));

        // 사용자가 해당 프로젝트에 참여하고 있는지 확인
        UserProject userProject = userProjectRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new RuntimeException("사용자가 해당 프로젝트에 참여하고 있지 않습니다"));

        // 일정 생성
        Task task = new Task(user, project, taskRequest.getDescription(), taskRequest.getStart(), taskRequest.getEnd(), false);
        Task savedTask = taskRepository.save(task);

        return new TaskResponse(savedTask.getId(), user.getId(), project.getId(), task.getDescription(), task.getStart(), task.getEnd(), task.getAttainment());
    }

    // 전체 일정 조회
    public List<TaskResponse> getTasks(User user, Long projectId) {
        // 프로젝트가 존재하는지 확인
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("해당 프로젝트가 존재하지 않습니다"));

        userProjectRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new RuntimeException("사용자가 해당 프로젝트에 참여하고 있지 않습니다"));

        // 프로젝트의 모든 일정 조회
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream().map(task -> new TaskResponse(task.getId(), task.getUser().getId(), task.getProject().getId(),
                        task.getDescription(), task.getStart(), task.getEnd(), task.getAttainment()))
                .collect(Collectors.toList());
    }

    // 일정 수정
    public TaskResponse updateTask(User user, Long taskId, TaskRequest taskRequest) {
        // 일정이 존재하는지 확인
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("해당 일정이 존재하지 않습니다"));

        // 해당 일정이 속한 프로젝트에 사용자가 참여하고 있는지 확인
        Project project = task.getProject();
        userProjectRepository.findByUserAndProject(user, project)
                .orElseThrow(() -> new RuntimeException("사용자가 해당 프로젝트에 참여하고 있지 않습니다"));

        // 일정 수정
        task.setDescription(taskRequest.getDescription());
        task.setStart(taskRequest.getStart());
        task.setEnd(taskRequest.getEnd());
        task.setAttainment(taskRequest.getAttainment());

        Task updatedTask = taskRepository.save(task);

        return new TaskResponse(updatedTask.getId(), user.getId(), updatedTask.getProject().getId(),
                updatedTask.getDescription(), updatedTask.getStart(), updatedTask.getEnd(), updatedTask.getAttainment());
    }
}
