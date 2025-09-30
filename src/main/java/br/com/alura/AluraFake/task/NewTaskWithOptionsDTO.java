package br.com.alura.AluraFake.task;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

public class NewTaskWithOptionsDTO extends NewTaskDTO {

	@Valid
	private List<NewOptionDTO> options; 
	
	public NewTaskWithOptionsDTO() {}
	
	public List<NewOptionDTO> getOptions() {
		return options;
	}

	public void setOptions(List<NewOptionDTO> options) {
		this.options = new ArrayList<NewOptionDTO>();
		if (options != null)
			this.options.addAll(options);
	}

}
