package br.com.alura.AluraFake.task;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
@DynamicUpdate
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime createdAt = LocalDateTime.now();
	private String statement;
	@Column(name = "_order")
	private Integer order;
	@Enumerated(EnumType.STRING)
	@Column(name = "_type")
	private Type type;
	@ManyToOne
	private Course course;

	@Deprecated
	public Task() {
	}

	public Task(Course course, String statement, Integer order, Type type) {
		this.course = course;
		this.statement = statement;
		this.order = order;
		this.type = type;
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

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
