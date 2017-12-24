package bgu.spl.a2.sim.actions;


import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author nadav.
 * this class is in actor of Department
 */
public class CloseCourseAction extends Action<Boolean> {
    private final String Course;
    private final String Department;

    public CloseCourseAction(String course, String department) {
        this.Course = course;
        this.Department = department;
    }

    @Override
    protected void start() {
        this.name = "Close Course";
        actionState.addRecord(name);
        ArrayList<Action<?>> actions = new ArrayList<>();
        Action getStudentList = new Action<List<String>>() {
            @Override
            //in actor of Course
            protected void start() {
                ((CoursePrivateState) this.actionState).setAvailableSpots(-1);
                complete(((CoursePrivateState) this.actionState).getRegStudents());
            }
        };
        actions.add(getStudentList);
        then(actions, () -> {
            List<String> studentList = (List<String>) getStudentList.getResult().get();
            if (studentList == null) {
                complete(false);
                return;
            }
            ArrayList<UnregisterAction> unregisterActions = new ArrayList<>();
            studentList.forEach((student) -> {
                UnregisterAction unregister = new UnregisterAction();
                unregister.setCourse(Course);
                unregister.setStudent(student);
                unregisterActions.add(unregister);
            });
            then(unregisterActions, () -> {
                AtomicBoolean flag = new AtomicBoolean(true);
                unregisterActions.forEach((action -> flag.compareAndSet(true, (Boolean) action.getResult().get())));
                complete(flag.get());
            });
            actions.forEach((unregister)->sendMessage(unregister, Course, new CoursePrivateState()));
        });
        sendMessage(getStudentList, Course, new CoursePrivateState());
    }
}
