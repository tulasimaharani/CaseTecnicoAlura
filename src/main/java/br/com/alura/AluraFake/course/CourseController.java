package br.com.alura.AluraFake.course;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.task.Type;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;

@RestController
public class CourseController {

	private final CourseRepository courseRepository;
	private final UserRepository userRepository;
	private final TaskRepository taskRepository;

	@Autowired
	public CourseController(CourseRepository courseRepository, UserRepository userRepository,
			TaskRepository taskRepository) {
		this.courseRepository = courseRepository;
		this.userRepository = userRepository;
		this.taskRepository = taskRepository;
	}

	@Transactional
	@PostMapping("/course/new")
	public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {

		// Caso implemente o bonus, pegue o instrutor logado
		Optional<User> possibleAuthor = userRepository.findByEmail(newCourse.getEmailInstructor())
				.filter(User::isInstructor);

		if (possibleAuthor.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorItemDTO("emailInstructor", "Usuário não é um instrutor"));
		}

		Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());

		courseRepository.save(course);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/course/all")
	public ResponseEntity<List<CourseListItemDTO>> createCourse() {
		List<CourseListItemDTO> courses = courseRepository.findAll().stream().map(CourseListItemDTO::new).toList();
		return ResponseEntity.ok(courses);
	}

	@Transactional
	@PostMapping("/course/{id}/publish")
	public ResponseEntity createCourse(@PathVariable("id") Long id) {
		Optional<Course> course = courseRepository.findById(id);

		if (course.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(new ErrorItemDTO("publish", "Curso não encontrado"));
		}

		if (!course.get().getStatus().equals(Status.BUILDING)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorItemDTO("publish", "O curso só pode ser publicado se o status for BUILDING"));
		}

		for (Type type : Arrays.asList(Type.values())) {
			if (!taskRepository.existsByType(type))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(new ErrorItemDTO("publish", "O curso deve conter ao menos uma atividade de cada tipo."));
		}

		course.get().setStatus(Status.PUBLISHED);
		courseRepository.save(course.get());
		return ResponseEntity.ok().build();
	}

}
