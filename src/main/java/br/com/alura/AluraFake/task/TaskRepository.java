package br.com.alura.AluraFake.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

	boolean existsByType(Type type);

	List<Task> findAllByCourseId(Long long1);

}
