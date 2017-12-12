package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;

/**
 * @author nadav.
 * this action should be in course actor
 *
 */
public class UnregisterAction extends Action<Boolean>{
    private final String student;
    private final String course;

    public UnregisterAction(String student,String course) {
        this.student = student;
        this.course = course;
    }

    @Override
    protected void start() {
        this.name = "Unregister";
        actionState.addRecord(name);
        ArrayList<Action<?>> actions= new ArrayList<>();
        Action removeCourseFromStudent=new Action<Boolean>(){
            //in actor of a student
            @Override
            protected void start() {
                complete(((StudentPrivateState) this.actionState).removeGrade(course));
            }
        };
        actions.add(removeCourseFromStudent);
        sendMessage(removeCourseFromStudent,student,new StudentPrivateState());
        then(actions,() -> {
            boolean unregistered=((CoursePrivateState)actionState).unregisterStudent(student);
            complete(unregistered);
        });
    }
}
