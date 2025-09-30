package br.com.alura.AluraFake.task;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.util.ErrorItem;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;
	
	@Autowired
	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}
	
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

	public void checkNumberOfOptionsMultipleChoice(NewTaskWithOptionsDTO newTaskDTO) throws ErrorItem {
		int numberOfOptions = newTaskDTO.getOptions().size();
		if (numberOfOptions < 3 || numberOfOptions > 5) {
			throw new ErrorItem("option", "A atividade deve ter no minimo 3 e no máximo 5 alternativas");
		}
	}

	public void checkNumberOfCorrectAndIncorrectAnswersMultipleChoice(NewTaskWithOptionsDTO newTaskDTO) throws ErrorItem {
		int numberOfCorrectAnswers = 0;
		int numberOfIncorrectAnswers = 0;
		for (NewOptionDTO option : newTaskDTO.getOptions()) {
			if (option.isCorrect()) {
				numberOfCorrectAnswers++;
			} else {
				numberOfIncorrectAnswers++;
			}
		}
		if (numberOfCorrectAnswers < 2 || numberOfIncorrectAnswers < 1) {
			throw new ErrorItem("option",
					"A atividade deve ter duas ou mais alternativas corretas, e ao menos uma alternativa incorreta.");
		}
	}

	public void checkTaskStatementDiffersFromOption(NewTaskWithOptionsDTO newTaskDTO) throws ErrorItem {
		for (NewOptionDTO option : newTaskDTO.getOptions()) {
			if (option.getOption().equalsIgnoreCase(newTaskDTO.getStatement())) {
				throw new ErrorItem("option", "As alternativas não podem ser iguais ao enunciado da atividade.");
			}
		}
	}
	
	public void fixTaskOrder(Task taskToFix) throws ErrorItem {
		List<Task> tasks = taskRepository.findAllByOrderByOrder();
		if(tasks.isEmpty() && taskToFix.getOrder() != 1) {
			throw new ErrorItem("order", "A sequência de atividades deve começar em 1");
		}
		if(tasks.isEmpty())
			return;
		int orderToMove = 0;
		if (tasks.getLast().getOrder()+1 == taskToFix.getOrder()) {
			return;
		} 
		if (tasks.getLast().getOrder()+1 < taskToFix.getOrder()) {
			throw new ErrorItem("order", "A sequência de atividades está incorreta");
		}
		if (tasks.getLast().getOrder() >= taskToFix.getOrder()) {
			orderToMove = taskToFix.getOrder();
		}
		for (int i = orderToMove; i <= tasks.size(); i++) {
			tasks.get(i-1).setOrder(i+1);
			taskRepository.save(tasks.get(i-1));
		}
	}

}