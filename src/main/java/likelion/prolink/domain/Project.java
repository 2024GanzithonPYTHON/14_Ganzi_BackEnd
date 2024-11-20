package likelion.prolink.domain;


import jakarta.persistence.*;
import likelion.prolink.listener.ProjectListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;


@Entity @Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(ProjectListener.class)
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectName;
    private String title;
    private String grade;
    private Boolean isRecruit;
    private Boolean isActive;
    private Long contributorNum;
    private String category;
    private String recruitmentPosition;
    private String content;
    private String link;
    private LocalDate deadLine;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
