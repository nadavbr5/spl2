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
public class OpenANewCoueseAction extends Action<Boolean> {

    private final String courseName;
    private final CoursePrivateState coursePrivateState;

    public OpenANewCoueseAction(String name, int availableSpots, List<String> prequisites) {
        this.courseName = name;
        this.coursePrivateState = new CoursePrivateState();
        this.coursePrivateState.setAvailableSpots(availableSpots);
        this.coursePrivateState.setPrequisites(prequisites);
    }

    @Override
    protected void start() {
        ArrayList<Action<?>> actions = new ArrayList<>();
        CreateNewActorAction createNewActorAction = new CreateNewActorAction();
        actions.add(createNewActorAction);
        sendMessage(createNewActorAction, courseName, this.coursePrivateState);
        then(actions, () -> {
            ((DepartmentPrivateState) this.actionState).addCourse(courseName);
            complete((createNewActorAction.getResult().get()));
        });
    }

}
