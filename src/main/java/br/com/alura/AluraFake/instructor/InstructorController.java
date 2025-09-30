package br.com.alura.AluraFake.instructor;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;

@RestController
public class InstructorController {

	private final UserRepository userRepository;
	private final CourseRepository courseRepository;
	private final TaskRepository taskRepository;

	public InstructorController(UserRepository userRepository, CourseRepository courseRepository,
			TaskRepository taskRepository) {
		this.userRepository = userRepository;
		this.courseRepository = courseRepository;
		this.taskRepository = taskRepository;
	}

	@GetMapping("/instructor/{id}/courses")
	public ResponseEntity listAllCoursesByInstructor(@PathVariable("id") Long id) {
		Optional<User> instructor = userRepository.findById(id);

		if (instructor.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorItemDTO("id", "Instrutor não encontrado"));
		}
		if (!instructor.get().isInstructor()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new ErrorItemDTO("id", "Usuário não é instrutor"));
		}
		
		List<CourseListItemByInstructorDTO> courses = courseRepository.findAllByInstructorId(id).stream()
				.map(CourseListItemByInstructorDTO::new).toList();
		if (courses.isEmpty()) {
			return ResponseEntity.ok(new CoursesListByInstructorDTO(courses, 0L));
		}

		courses.forEach(course -> course.setNumberOfTasks(taskRepository.findAllByCourseId(course.getId()).size()));
		Long numberOfPublishedCourses = courses.stream().filter(course -> course.getStatus().equals(Status.PUBLISHED))
				.count();
		CoursesListByInstructorDTO courseListByInstructorDTO = new CoursesListByInstructorDTO(courses,
				numberOfPublishedCourses);

		return ResponseEntity.ok(courseListByInstructorDTO);
	}

}
