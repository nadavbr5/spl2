package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * this action should be in student actor
 */

public class CheckAndSignAction extends Action<Boolean> {
    private String computerType;
    private List<String> courses;

    public CheckAndSignAction(String computerType, List<String> courses) {
        this.computerType = computerType;
        this.courses = courses;
    }

    @Override
    protected void start() {
        HashMap<String, Integer> grades = ((StudentPrivateState) this.actionState).getGrades();
        Promise<Computer> promise = Simulator.warehouse.checkAndSign(computerType);
        promise.subscribe(() ->  {
            AtomicLong sig = new AtomicLong();
            sig.set(promise.get().checkAndSign(courses, grades));
            Action setSignature = new Action<Boolean>() {
                @Override
                protected void start() {
                    ((StudentPrivateState) this.actionState).setSignature(sig.get());
                    complete(true);
                }
            };
            sendMessage(setSignature, this.actionActor, new StudentPrivateState());
            ArrayList<Action<?>> actions = new ArrayList<>();
            actions.add((setSignature));
            then(actions,()->{complete((Boolean) setSignature.getResult().get());});
        });
    }
}
