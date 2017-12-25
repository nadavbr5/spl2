package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author nadav.
 * this action should be in Course actor
 */
public class UnregisterAction extends Action<Boolean> {
    private String Student;
    private String Course;

    @Override
    protected void start() {
        this.name = "Unregister";
        actionState.addRecord(name);
        EmptyAction waitUntilRegister = new EmptyAction();
        then(Collections.singletonList(waitUntilRegister), () -> {
            boolean unregistered = ((CoursePrivateState) actionState).unregisterStudent(Student);
            if (!unregistered) {
                complete(false);
                return;
            }
            Action<Boolean> removeCourseFromStudent = new Action<Boolean>() {
                //in actor of a Student
                @Override
                protected void start() {
                    complete(((StudentPrivateState) this.actionState).removeGrade(Course));
                }
            };
            then(Collections.singletonList(removeCourseFromStudent), () -> {
                complete(removeCourseFromStudent.getResult().get());
            });
            sendMessage(removeCourseFromStudent, Student, new StudentPrivateState());
        });
        sendMessage(waitUntilRegister, Student, new StudentPrivateState());
    }

    public void setStudent(String student) {
        Student = student;
    }

    public void setCourse(String course) {
        Course = course;
    }
}
