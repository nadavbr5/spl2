package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;

/**
 * this action should be in department actor
 */

public class AddStudent extends Action<Boolean> {
    private String Student;

    @Override
    protected void start() {
        this.name = "Add Student";
        actionState.addRecord(name);
        ArrayList<Action<?>> actions = new ArrayList<>();
        EmptyAction emptyAction = new EmptyAction();
        actions.add(emptyAction);
        then(actions, () -> {
            ((DepartmentPrivateState) this.actionState).addStudent(Student);
            complete((emptyAction.getResult().get()));
        });
        sendMessage(emptyAction, Student, new StudentPrivateState());
    }
}
