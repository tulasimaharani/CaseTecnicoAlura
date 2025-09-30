package br.com.alura.AluraFake.task;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity(name = "_Option")
public class Option {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private LocalDateTime createdAt = LocalDateTime.now();
	@Column(name = "_option")
	private String option;
	private Boolean isCorrect;
	@ManyToOne
	private Task task;
	
	public Option(String option, Boolean isCorrect, Task task) {
		this.option = option;
		this.isCorrect = isCorrect;
		this.task = task;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public Boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public Long getId() {
		return id;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

}
