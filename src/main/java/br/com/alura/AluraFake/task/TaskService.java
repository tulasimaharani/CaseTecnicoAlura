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

}