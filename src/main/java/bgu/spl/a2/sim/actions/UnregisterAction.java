package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;

/**
 * @author nadav.
 * this action should be in course actor
 *
 */
public class UnregisterAction extends Action<Boolean>{
    private final String course;
    private final String student;

    public UnregisterAction(String student, String course) {
        this.student = student;
        this.course = course;
    }

    @Override
    protected void start() {
        ArrayList<Action<Boolean>> actions= new ArrayList<>();
        removeCourseFromStudentAction removeCourse = new removeCourseFromStudentAction(course);
        actions.add(removeCourse);
        sendMessage(removeCourse,student,new StudentPrivateState());
        then(actions,() -> {
            ((CoursePrivateState)actionState).unregisterStudent(student);

            complete(true);
        });
    }
}
