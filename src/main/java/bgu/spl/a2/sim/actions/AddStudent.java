package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;

/**
 * this action should be in department actor
 *
 */

public class AddStudent extends Action<Boolean> {
    private final String studentName;

    public AddStudent(String name){
        this.studentName= name;
    }
    @Override
    protected void start() {
        ArrayList<Action<?>> actions = new ArrayList<>();
        CreateNewActorAction createNewActorAction = new CreateNewActorAction();
        actions.add(createNewActorAction);
        sendMessage(createNewActorAction, studentName, new StudentPrivateState());
        then(actions, () -> {
            ((DepartmentPrivateState) this.actionState).addStudent(studentName);
            complete((createNewActorAction.getResult().get()));
        });
    }
}