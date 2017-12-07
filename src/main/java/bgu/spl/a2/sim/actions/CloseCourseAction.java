package bgu.spl.a2.sim.actions;


import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author nadav.
 * this class is in actor of department
 */
public class CloseCourseAction extends Action<Boolean> {
    private final String course;
    private final String department;

    public CloseCourseAction(String course, String department) {
        this.course = course;
        this.department = department;
    }

    @Override
    protected void start() {
        ArrayList<Action<?>> actions = new ArrayList<>();
        Action getStudentList = new Action<List<String>>() {
            @Override
            //in actor of course
            protected void start() {
                ((CoursePrivateState) this.actionState).setAvailableSpots(-1);
                complete(((CoursePrivateState) this.actionState).getRegStudents());
            }
        };
        actions.add(getStudentList);
        sendMessage(getStudentList, course, new CoursePrivateState());
        then(actions, () -> {
            List<String> studentList = (List<String>) getStudentList.getResult().get();
            if (studentList == null) {
                complete(false);
                return;
            }
            ArrayList<Action<?>> unregisterActions = new ArrayList<>();
            studentList.forEach((student) -> {
                UnregisterAction unregister = new UnregisterAction(student, course);
                sendMessage(unregister, course, new CoursePrivateState());
                unregisterActions.add(unregister);
            });
            then(unregisterActions, () -> {
                AtomicBoolean flag = new AtomicBoolean(true);
                unregisterActions.forEach((action -> flag.compareAndSet(true, (Boolean) action.getResult().get())));
                complete(flag.get());
            });
        });
    }
}
