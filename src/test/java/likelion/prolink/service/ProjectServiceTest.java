package likelion.prolink.service;

import likelion.prolink.domain.Category;
import likelion.prolink.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProjectTest() {
        Project mockProject = new Project();
        mockProject.setId(1L);
        mockProject.setProjectName("test name");
        mockProject.setTitle("test title");
        mockProject.setGrade("test grade");

        when(projectRepository.save(org.mockito.ArgumentMatchers.any(Project.class))).thenReturn(mockProject);

        ProjectResponseDto response = projectService.createProject(new ProjectRequestDto(
                "test name",
                "test title",
                "test grade",
                true,
                true,
                5,
                Category.DESIGN,
                "test position",
                "test content",
                LocalDateTime.now().plusMonths(1)
        ));

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getProjectName()).isEqualTo("test name");
        assertThat(response.getTitle()).isEqualTo("test title");
        assertThat(response.getGrade()).isEqualTo("test grade");
    }
}
