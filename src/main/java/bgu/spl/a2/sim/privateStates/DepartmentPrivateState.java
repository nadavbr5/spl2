package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe department's private state
 */
public class DepartmentPrivateState extends PrivateState{
	private List<String> courseList;
	private List<String> studentList;
	
	/**
 	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 */
	public DepartmentPrivateState() {
		this.courseList=new ArrayList<>();
		this.studentList=new ArrayList<>();
	}

	public List<String> getCourseList() {
		return courseList;
	}

	public List<String> getStudentList() {
		return studentList;
	}

	public void addCourse(String course) {
		courseList.add(course);
	}

	public void addStudent(String student) {
		studentList.add(student);
	}
	public void removeCourse(String course) {
		courseList.remove(course);
	}

	public void removeStudent(String student) {
		studentList.remove(student);
	}
}
