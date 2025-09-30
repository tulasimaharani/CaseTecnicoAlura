package br.com.alura.AluraFake.task;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.ErrorItem;

@Service
public class TaskService {

	public void checkCourseStatus(Optional<Course> course) throws ErrorItem {
		if (!course.get().getStatus().equals(Status.BUILDING)) {
			throw new ErrorItem("courseId", "Curso não está em construção");
		}
	}

	public void checkNumberOfOptionsSingleChoice(NewTaskWithOptionsDTO newTaskDTO) throws ErrorItem {
		int numberOfOptions = newTaskDTO.getOptions().size();
		if (numberOfOptions < 2 || numberOfOptions > 5) {
			throw new ErrorItem("option", "A atividade deve ter no minimo 2 e no máximo 5 alternativas");
		}
	}

	public void checkNumberOfCorrectAnswersSingleChoice(NewTaskWithOptionsDTO newTaskDTO) throws ErrorItem {
		int numberOfCorrectAnswers = 0;
		for (NewOptionDTO option : newTaskDTO.getOptions()) {
			if (option.isCorrect()) {
				numberOfCorrectAnswers++;
			}
			if (numberOfCorrectAnswers > 1) {
				throw new ErrorItem("option", "Apenas uma alternativa deve ser verdadeira");
			}
		}
		if (numberOfCorrectAnswers == 0) {
			throw new ErrorItem("option", "Uma das alternativas deve ser verdadeira");
		}
	}

}