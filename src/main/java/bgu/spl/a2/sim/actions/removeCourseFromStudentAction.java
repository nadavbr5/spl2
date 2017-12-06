package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

/**
 * @author nadav.
 */
public class removeCourseFromStudentAction extends Action<Boolean> {
    private final String course;

    public removeCourseFromStudentAction(String course) {
        this.course = course;
    }
    @Override
    protected void start() {
        ((StudentPrivateState)actionState).removeGrade(course);
        complete(true);
    }
}
