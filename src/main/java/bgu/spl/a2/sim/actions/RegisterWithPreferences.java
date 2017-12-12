package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author nadav.
 * this action should be in department actor
 */
public class RegisterWithPreferences extends Action<Boolean> {
    private List<String> courses;
    private List<Integer> grades;
    private String student;


    public RegisterWithPreferences(String student, List<String> courses, List<Integer> grades) {
        this.courses = courses;
        this.grades = grades;
        this.student = student;
    }
    @Override
    protected void start() {
        this.name = "Register With Preferences";
        actionState.addRecord(name);
        AtomicBoolean registered=new AtomicBoolean();
        complete(tryToRegister(registered));

    }

    private boolean tryToRegister(AtomicBoolean registered) {
        if(courses.isEmpty())
            return false;
        String course = courses.remove(0);
            Integer grade = grades.remove(0);
            ParticipatingInCourseAction action = new ParticipatingInCourseAction(student, grade);
            sendMessage(action, course, new CoursePrivateState());
            action.getResult().subscribe(()->{
                if (action.getResult().get()) {
                    registered.compareAndSet(false, true);
                }else
                    tryToRegister(registered);
            });
        return registered.get();
    }
}