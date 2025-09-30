package br.com.alura.AluraFake.instructor;

import java.io.Serializable;
import java.time.LocalDateTime;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;

public class CourseListItemByInstructorDTO implements Serializable {

    private Long id;
    private String title;
    private String description;
    private Status status;
    private LocalDateTime publishedAt;
    private Integer numberOfTasks;
    
    public CourseListItemByInstructorDTO(Course course) {
        this.id = course.getId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.status = course.getStatus();
        this.publishedAt = course.getPublishedAt();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}

	public Integer getNumberOfTasks() {
		return numberOfTasks;
	}
	
	public void setNumberOfTasks(Integer numberOfTasks) {
		this.numberOfTasks = numberOfTasks;
	}
    
}
