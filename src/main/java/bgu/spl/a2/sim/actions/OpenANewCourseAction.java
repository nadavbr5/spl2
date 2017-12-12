package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;

/**
 * this action should be in department actor
 */
public class OpenANewCourseAction extends Action<Boolean> {

    private final String courseName;
    private final CoursePrivateState coursePrivateState;

    public OpenANewCourseAction(String name, int availableSpots, List<String> prequisites) {
        this.courseName = name;
        this.coursePrivateState = new CoursePrivateState();
        this.coursePrivateState.setAvailableSpots(availableSpots);
        this.coursePrivateState.setPrequisites(prequisites);
    }

    @Override
    protected void start() {
        this.name = "Open Course";
        actionState.addRecord(name);
        ArrayList<Action<?>> actions = new ArrayList<>();
        CreateNewActorAction createNewActorAction = new CreateNewActorAction();
        actions.add(createNewActorAction);
        then(actions, () -> {
            ((DepartmentPrivateState) this.actionState).addCourse(courseName);
            complete((createNewActorAction.getResult().get()));
        });
        sendMessage(createNewActorAction, courseName, this.coursePrivateState);
    }

}
