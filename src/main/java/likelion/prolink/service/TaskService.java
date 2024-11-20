package likelion.prolink.service;

import likelion.prolink.domain.*;
import likelion.prolink.domain.dto.request.TaskRequest;
import likelion.prolink.domain.dto.response.TaskResponse;
import likelion.prolink.repository.ProjectRepository;
import likelion.prolink.repository.TaskRepository;
import likelion.prolink.repository.UserProjectRepository;
import likelion.prolink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserProjectRepository userProjectRepository;
    private final GlobalSevice globalSevice;
    private final UserRepository userRepository;

    // 일정 저장
    public TaskResponse createTask(CustomUserDetails customUserDetails, Long projectId, TaskRequest taskRequest) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        // 일정 생성
        Task task = new Task(user, project, taskRequest.getDescription(), taskRequest.getStart(), taskRequest.getEnd(), false);
        Task savedTask = taskRepository.save(task);

        return new TaskResponse(savedTask.getId(), savedTask.getUser().getId(), savedTask.getProject().getId(), savedTask.getDescription(), savedTask.getStart(), savedTask.getEnd(), savedTask.getAttainment());
    }

    // 전체 일정 조회
    public List<TaskResponse> getTasks(CustomUserDetails customUserDetails, Long projectId) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        // 프로젝트의 모든 일정 조회
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream().map(task -> new TaskResponse(task.getId(), task.getUser().getId(), task.getProject().getId(),
                        task.getDescription(), task.getStart(), task.getEnd(), task.getAttainment()))
                .collect(Collectors.toList());
    }

    // 일정 수정
    public TaskResponse updateTask(CustomUserDetails customUserDetails, Long projectId , Long taskId, TaskRequest taskRequest) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        Task task =  taskRepository.findById(taskId).orElseThrow(()-> new NotFoundException("해당 일정을 찾을 수 없습니다."));

        // 일정 수정
        task.setDescription(taskRequest.getDescription());
        task.setStart(taskRequest.getStart());
        task.setEnd(taskRequest.getEnd());

        Task updatedTask = taskRepository.save(task);

        return new TaskResponse(updatedTask.getId(), user.getId(), updatedTask.getProject().getId(),
                updatedTask.getDescription(), updatedTask.getStart(), updatedTask.getEnd(), updatedTask.getAttainment());
    }

    public TaskResponse toggleTask(CustomUserDetails customUserDetails, Long projectId , Long taskId) {
        User user = globalSevice.findUser(customUserDetails);
        Project project = projectRepository.findById(projectId).orElseThrow(()-> new NotFoundException("해당 프로젝트를 찾을 수 없습니다."));

        UserProject userProject = userProjectRepository.findByUserAndProjectAndIsAcceptedTrue(user,project)
                .orElseThrow(()-> new NotFoundException("해당 프로젝트에 속해 있지 않습니다"));

        Task task =  taskRepository.findById(taskId).orElseThrow(()-> new NotFoundException("해당 일정을 찾을 수 없습니다."));

        Task updatedTask;

        if (!task.getAttainment()) {
            task.setAttainment(true);
            userProjectRepository.findByProject(userProject.getProject()).stream()
                    .filter(up -> up.getIsAccepted())
                    .forEach(up -> {
                        up.getUser().setPoint(up.getUser().getPoint() + 1);
                        userRepository.save(up.getUser());
                    });
            updatedTask = taskRepository.save(task);
        } else {
            task.setAttainment(false);
            userProjectRepository.findByProject(userProject.getProject()).stream()
                    .filter(up -> up.getIsAccepted())
                    .forEach(up -> {
                        up.getUser().setPoint(up.getUser().getPoint() - 1);
                        userRepository.save(up.getUser());
                    });
            updatedTask = taskRepository.save(task);
        }

        return new TaskResponse(updatedTask.getId(), user.getId(), updatedTask.getProject().getId(),
                updatedTask.getDescription(), updatedTask.getStart(), updatedTask.getEnd(), updatedTask.getAttainment());
    }
}
