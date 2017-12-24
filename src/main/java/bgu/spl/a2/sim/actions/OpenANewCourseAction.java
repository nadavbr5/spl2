package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * this action should be in department actor
 */
public class OpenANewCourseAction extends Action<Boolean> {

    private String Course;
    private Integer Space;
    private List<String> Prerequisites = new ArrayList<>();


    @Override
    protected void start() {
        this.name = "Open Course";
        actionState.addRecord(name);
        ArrayList<Action<?>> actions = new ArrayList<>();
        CreateNewActorAction createNewActorAction = new CreateNewActorAction();
        CoursePrivateState coursePrivateState=new CoursePrivateState();
        coursePrivateState.setAvailableSpots(Space);
        coursePrivateState.setPrequisites(Prerequisites);
        actions.add(createNewActorAction);
        then(actions, () -> {
            ((DepartmentPrivateState) this.actionState).addCourse(Course);
            complete((createNewActorAction.getResult().get()));
        });
        sendMessage(createNewActorAction, Course, coursePrivateState);
    }

}
