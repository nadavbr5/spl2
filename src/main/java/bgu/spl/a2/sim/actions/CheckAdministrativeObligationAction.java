package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this action should be in department actor
 *
 */
public class CheckAdministrativeObligationAction extends Action<Boolean> {
    private List<String> students;
    private String computerType;
    private List<String> courses;

    public CheckAdministrativeObligationAction(List<String> students,String computerType, List<String> courses){
        this.students= students;
        this.computerType=computerType;
        this.courses= courses;
    }

    @Override
    protected void start() {
        this.name = "Administrative Check";
        actionState.addRecord(name);
        ArrayList<CheckAndSignAction> actions = new ArrayList<>();
        students.forEach((student) -> {
            CheckAndSignAction action = new CheckAndSignAction(computerType, courses);
            actions.add(action);
            sendMessage(action, student, new StudentPrivateState());
        });
        then(actions, () -> {
            AtomicBoolean ans = new AtomicBoolean(true);
            actions.forEach((action) -> {
                if (!action.getResult().get()) {
                    ans.compareAndSet(true, false);
                }

            });
            complete(ans.get());
        });
    }
}
