package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author nadav.
 * this action should be in Student actor
 */
public class RegisterWithPreferences extends Action<Boolean> {
    private List<String> Preferences =new ArrayList<>();
    private List<Integer> Grade =new ArrayList<>();
    private String Student;



    @Override
    protected void start() {
        this.name = "Register With Preferences";
        actionState.addRecord(name);
        AtomicBoolean registered=new AtomicBoolean();
        complete(tryToRegister(registered));

    }

    private boolean tryToRegister(AtomicBoolean registered) {
        if(Preferences.isEmpty())
            return false;
        String course = Preferences.remove(0);
            Integer grade = Grade.remove(0);
            ParticipatingInCourseAction action = new ParticipatingInCourseAction();
        action.setStudent(Student);
        action.setGrade(grade);
        action.getResult().subscribe(()->{
            if (action.getResult().get()) {
                registered.compareAndSet(false, true);
            }else
                tryToRegister(registered);
        });
        sendMessage(action, course, new CoursePrivateState());
        return registered.get();
    }
}
