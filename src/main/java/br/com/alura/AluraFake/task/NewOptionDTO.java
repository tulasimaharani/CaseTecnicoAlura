package br.com.alura.AluraFake.task;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public class NewOptionDTO {

	@NotNull
	@Length(min = 3, max = 80)
	private String option;
	@NotNull
	private Boolean isCorrect;
	
	public NewOptionDTO(String option, Boolean isCorrect) {
		this.option = option;
		this.isCorrect = isCorrect;
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
	
}
