package br.com.alura.AluraFake.task;

import java.time.LocalDateTime;
import java.util.ArrayList;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime createdAt = LocalDateTime.now();
	@ManyToOne
	private Course course;
	private String statement;
	private Integer order;
	@OneToMany
	private ArrayList<Option> options;
	
	public Task(Course course, String statement, Integer order) {
		this.course = course;
		this.statement = statement;
		this.order = order;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

}
