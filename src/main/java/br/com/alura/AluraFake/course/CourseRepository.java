package br.com.alura.AluraFake.course;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long>{

	List<Course> findAllByInstructorId(Long id);

}
