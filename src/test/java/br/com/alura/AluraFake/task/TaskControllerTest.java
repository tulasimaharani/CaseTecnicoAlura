package br.com.alura.AluraFake.task;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private TaskRepository taskRepository;
	@MockBean
	private OptionRepository optionRepository;
	@MockBean
	private CourseRepository courseRepository;
	@MockBean
	private TaskService taskService;
	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void newTaskDTO__should_return_bad_request_when_course_does_not_exists() throws Exception {
		NewTaskDTO newTaskDTO = new NewTaskDTO();
		newTaskDTO.setCourseId(999L);
		newTaskDTO.setStatement("O que aprendemos na aula de hoje?");
		newTaskDTO.setOrder(1);
	   
		doReturn(Optional.empty()).when(courseRepository).findById(any());

		mockMvc.perform(post("/task/new/opentext").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.field").value("courseId"))
				.andExpect(jsonPath("$.message").value("Curso inválido"));
	}

	@Test
	void newTaskDTO__should_return_bad_request_when_course_is_not_building() throws Exception {
		NewTaskDTO newTaskDTO = new NewTaskDTO();
		newTaskDTO.setCourseId(1L);
		newTaskDTO.setStatement("O que aprendemos na aula de hoje?");
		newTaskDTO.setOrder(1);

		Course course = mock(Course.class);
		doReturn(Status.PUBLISHED).when(course).getStatus();
		doReturn(Optional.of(course)).when(courseRepository).findById(any());

		mockMvc.perform(post("/task/new/opentext").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(jsonPath("$.field").value("courseId"))
				.andExpect(jsonPath("$.message").value("Curso não está em construção"));
	}

	@Test
	void newTaskDTO__should_return_bad_request_when_statement_is_equal_to_another_task_statement() throws Exception {
		NewTaskDTO newTaskDTO = new NewTaskDTO();
		newTaskDTO.setCourseId(1L);
		newTaskDTO.setStatement("O que aprendemos na aula de hoje?");
		newTaskDTO.setOrder(1);

		mockCourse(newTaskDTO);
		doThrow(new DataIntegrityViolationException(null)).when(taskRepository).save(any());

		mockMvc.perform(post("/task/new/opentext").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.field").value("statement"))
				.andExpect(jsonPath("$.message").value("O curso não pode ter duas questões com o mesmo enunciado"));
	}

	@Test
	void newTaskDTO__opentext_should_return_created_when_new_task_request_is_valid() throws Exception {
		NewTaskDTO newTaskDTO = new NewTaskDTO();
		newTaskDTO.setCourseId(1L);
		newTaskDTO.setStatement("O que aprendemos na aula?");
		newTaskDTO.setOrder(1);

		mockCourse(newTaskDTO);
		
		mockMvc.perform(post("/task/new/opentext").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isCreated());
	
		verify(taskRepository, times(1)).save(any(Task.class));
	}

	@Test
	void newTaskDTO__should_return_bad_request_for_statement_too_short() throws Exception {
		NewTaskDTO newTaskDTO = new NewTaskDTO();
		newTaskDTO.setCourseId(1L);
		newTaskDTO.setStatement("???");
		newTaskDTO.setOrder(1);

		mockMvc.perform(post("/task/new/opentext").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskDTO__should_return_bad_request_for_statement_too_long() throws Exception {
		NewTaskDTO newTaskDTO = new NewTaskDTO();
		newTaskDTO.setCourseId(1L);
		newTaskDTO.setStatement("a".repeat(256));
		newTaskDTO.setOrder(1);

		mockMvc.perform(post("/task/new/opentext").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskDTO__should_return_bad_request_for_invalid_order() throws Exception {
		NewTaskDTO newTaskDTO = new NewTaskDTO();
		newTaskDTO.setCourseId(1L);
		newTaskDTO.setStatement("O que aprendemos na aula de hoje?");
		newTaskDTO.setOrder(0);

		mockMvc.perform(post("/task/new/opentext").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}
	
/*
 * Single Choice Task Tests
 */

	private NewTaskWithOptionsDTO mockNewTaskDTO() {
		NewTaskWithOptionsDTO newTaskDTO = new NewTaskWithOptionsDTO();
		newTaskDTO.setCourseId(1L);
		newTaskDTO.setStatement("O que aprendemos hoje?");
		newTaskDTO.setOrder(2);
		return newTaskDTO;
	}
	
	private Course mockCourse(NewTaskDTO newTaskDTO) {
		Course course = mock(Course.class);
		course.setStatus(Status.BUILDING);
		doReturn(Optional.of(course)).when(courseRepository).findById(any());	
		return course;
	}
	
	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_created_when_new_task_request_is_valid() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();
		
		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", true));
		listOfOptions.add(new NewOptionDTO("Python", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskDTO.setOptions(listOfOptions);

		mockCourse(newTaskDTO);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isCreated());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_too_few_options() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();
		
		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", true));
		newTaskDTO.setOptions(listOfOptions);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.field").value("option"))
		.andExpect(jsonPath("$.message").value("A atividade deve ter no minimo 2 e no máximo 5 alternativas"));
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_too_many_options() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();
		
		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", true));
		listOfOptions.add(new NewOptionDTO("Python", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		listOfOptions.add(new NewOptionDTO("C", false));
		listOfOptions.add(new NewOptionDTO("C++", false));
		listOfOptions.add(new NewOptionDTO("C#", false));
		newTaskDTO.setOptions(listOfOptions);

		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_multiple_correct_answers() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();
		
		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", true));
		listOfOptions.add(new NewOptionDTO("Python", true));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskDTO.setOptions(listOfOptions);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_no_correct_answer() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();

		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", false));
		listOfOptions.add(new NewOptionDTO("Python", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskDTO.setOptions(listOfOptions);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_duplicate_options() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();

		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", true));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskDTO.setOptions(listOfOptions);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_when_option_equals_statement() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();
		
		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", true));
		listOfOptions.add(new NewOptionDTO("O que aprendemos hoje?", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskDTO.setOptions(listOfOptions);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_when_option_too_short() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();

		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Ja", true));
		listOfOptions.add(new NewOptionDTO("Python", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskDTO.setOptions(listOfOptions);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_when_option_too_long() throws Exception {
		NewTaskWithOptionsDTO newTaskDTO = mockNewTaskDTO();
		
		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("a".repeat(81), true));
		listOfOptions.add(new NewOptionDTO("Python", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskDTO.setOptions(listOfOptions);
		
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskDTO))).andExpect(status().isBadRequest());
	}

	@Test
	void newTaskWithOptionsDTO__singlechoice_should_return_bad_request_course_not_building() throws Exception {
		NewTaskWithOptionsDTO newTaskWithOptionsDTO = mockNewTaskDTO();

		List<NewOptionDTO> listOfOptions = new ArrayList<NewOptionDTO>();
		listOfOptions.add(new NewOptionDTO("Java", true));
		listOfOptions.add(new NewOptionDTO("Python", false));
		listOfOptions.add(new NewOptionDTO("Ruby", false));
		newTaskWithOptionsDTO.setOptions(listOfOptions);
			
		Course course1 = mock(Course.class);
		course1.setStatus(Status.PUBLISHED);
		doReturn(Optional.of(course1)).when(courseRepository).findById(any());	
	
		mockMvc.perform(post("/task/new/singlechoice").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(newTaskWithOptionsDTO))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.field").value("courseId"))
				.andExpect(jsonPath("$.message").value("Curso não está em construção"));
		
	}

	
/*
 * Multiple Choice Task Tests
 */

}