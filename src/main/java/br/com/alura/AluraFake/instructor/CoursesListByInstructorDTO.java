package br.com.alura.AluraFake.instructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CoursesListByInstructorDTO implements Serializable {

	private List<CourseListItemByInstructorDTO> courseList;
	private Long numberOfPublishedCourses;

	public CoursesListByInstructorDTO(List<CourseListItemByInstructorDTO> courseList, Long numberOfPublishedCourses) {
		this.courseList = new ArrayList<CourseListItemByInstructorDTO>();
		this.courseList.addAll(courseList);
		this.numberOfPublishedCourses = numberOfPublishedCourses;
	}

	public List<CourseListItemByInstructorDTO> getCourseList() {
		return courseList;
	}

	public Long getNumberOfPublishedCourses() {
		return numberOfPublishedCourses;
	}

}
