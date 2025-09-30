package br.com.alura.AluraFake.instructor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.OptionRepository;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.task.TaskRepository;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;

@WebMvcTest(InstructorController.class)
class InstructorControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private TaskRepository taskRepository;
	@MockBean
	private OptionRepository optionRepository;
	@MockBean
	private CourseRepository courseRepository;
	@MockBean
	private UserRepository userRepository;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void CoursesListByInstructorDTO__should_return_created_when_request_is_valid() throws Exception {
		User instructor = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
		Course course1 = new Course("Java", "", instructor);
		course1.setStatus(Status.PUBLISHED);
		Course course2 = new Course("Ruby", "", instructor);
		course2.setStatus(Status.BUILDING);
		Task task = new Task(course1, null, null, null); 
		Task task2 = new Task(course1, null, null, null); 
		
		when(userRepository.findById(2L)).thenReturn(Optional.of(instructor));
		when(courseRepository.findAllByInstructorId(2L)).thenReturn(List.of(course1, course2));
		when(taskRepository.findAllByCourseId(any())).thenReturn(List.of(task, task2));
		
		mockMvc.perform(get("/instructor/{id}/courses", 2).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.courseList.length()").value(2))
				.andExpect(jsonPath("$.numberOfPublishedCourses").value(1))
				.andExpect(jsonPath("$.courseList[0].title").value("Java"))
				.andExpect(jsonPath("$.courseList[0].numberOfTasks").value(2));
	}

	@Test
	void CoursesListByInstructorDTO__should_return_empty_list_when_no_courses() throws Exception {
		User instructor = new User("Paulo", "paulo@alura.com.br", Role.INSTRUCTOR);
		
		when(userRepository.findById(2L)).thenReturn(Optional.of(instructor));
		when(courseRepository.findAllByInstructorId(2L)).thenReturn(List.of());
		
		mockMvc.perform(get("/instructor/{id}/courses", 2).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.courseList").isEmpty())
				.andExpect(jsonPath("$.numberOfPublishedCourses").value(0));
	}

	@Test
	void CoursesListByInstructorDTO__should_return_404_when_user_not_found() throws Exception {
		mockMvc.perform(get("/instructor/{id}/courses", 999L).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.field").value("id"))
				.andExpect(jsonPath("$.message").value("Instrutor não encontrado"));
	}

	@Test
	void CoursesListByInstructorDTO__should_return_400_when_user_not_instructor() throws Exception {
		User instructor = new User("Paulo", "paulo@alura.com.br", Role.STUDENT);
		when(userRepository.findById(2L)).thenReturn(Optional.of(instructor));

		mockMvc.perform(get("/instructor/{id}/courses", 2L).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

}