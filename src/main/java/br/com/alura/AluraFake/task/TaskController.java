package br.com.alura.AluraFake.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.util.ErrorItem;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;

@RestController
public class TaskController {

	private final TaskRepository taskRepository;
	private final OptionRepository optionRepository;
	private final CourseRepository courseRepository;
	private final TaskService taskService;

	@Autowired
	public TaskController(TaskRepository taskRepository, OptionRepository optionRepository,
			CourseRepository courseRepository, TaskService taskService) {
		this.taskRepository = taskRepository;
		this.optionRepository = optionRepository;
		this.courseRepository = courseRepository;
		this.taskService = taskService;
	}

	@PostMapping("/task/new/opentext")
	public ResponseEntity newOpenTextExercise(@Valid @RequestBody NewTaskDTO newTaskDTO) {
		newTaskDTO.setType(Type.OPEN_TEXT);

		try {
			Optional<Course> course = courseRepository.findById(newTaskDTO.getCourseId());
			if (course.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ErrorItemDTO("courseId", "Curso inválido"));
			}
			taskService.checkCourseStatus(course);

			taskRepository.save(newTaskDTO.toModel(course.get()));
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorItemDTO("statement", "O curso não pode ter duas questões com o mesmo enunciado"));
		} catch (ErrorItem e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorItemDTO(e.getField(), e.getMessage()));
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}


    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }
	@PostMapping("/task/new/singlechoice")
	public ResponseEntity newSingleChoice(@Valid @RequestBody NewTaskWithOptionsDTO newTaskWithOptionsDTO) {
		newTaskWithOptionsDTO.setType(Type.SINGLE_CHOICE);

		if (newTaskWithOptionsDTO.getOptions().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorItemDTO("option", "A atividade deve ter no minimo 2 e no máximo 5 alternativas"));
		}
		try {
			taskService.checkNumberOfOptionsSingleChoice(newTaskWithOptionsDTO);
			taskService.checkNumberOfCorrectAnswersSingleChoice(newTaskWithOptionsDTO);
			taskService.checkTaskStatementDiffersFromOption(newTaskWithOptionsDTO);

			Optional<Course> course = courseRepository.findById(newTaskWithOptionsDTO.getCourseId());
			if (course.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ErrorItemDTO("courseId", "Curso inválido"));
			}
			taskService.checkCourseStatus(course);

			Task task = taskRepository.save(newTaskWithOptionsDTO.toModel(course.get()));

			List<Option> optionsToSave = new ArrayList<Option>();
			for (NewOptionDTO optionDTO : newTaskWithOptionsDTO.getOptions()) {
				optionsToSave.add(new Option(optionDTO.getOption(), optionDTO.isCorrect(), task));
			}

			optionRepository.saveAll(optionsToSave);
		} catch (DataIntegrityViolationException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new ErrorItemDTO("option", "O curso ou tarefa não pode ter duas questões com o mesmo enunciado"));
		} catch (ErrorItem e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorItemDTO(e.getField(), e.getMessage()));
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

}