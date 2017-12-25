package bgu.spl.a2.sim.actions;

import bgu.spl.a2.Action;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.Computer;
import bgu.spl.a2.sim.Simulator;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * this action should be in department actor
 */
public class CheckAdministrativeObligationAction extends Action<Boolean> {
    private List<String> Students;
    private String Computer;
    private List<String> Conditions;
    private ArrayList<HashMap<String, Integer>> gradesList;
    private ArrayList<Long> signaturesList;

    public CheckAdministrativeObligationAction() {
        gradesList = new ArrayList<>();
        signaturesList = new ArrayList<>();

    }

    @Override
    protected void start() {
        this.name = "Administrative Check";
        actionState.addRecord(name);
        ArrayList<Action<HashMap<String, Integer>>> gradesActionsList = new ArrayList<>();
        Students.forEach((student) -> {
            Action<HashMap<String, Integer>> getGrade = new Action<HashMap<String, Integer>>() {
                @Override
                protected void start() {
                    complete(((StudentPrivateState) this.actionState).getGrades());
                }
            };
            gradesActionsList.add(getGrade);
        });
        then(gradesActionsList, () -> {
            gradesActionsList.forEach((action) -> gradesList.add(action.getResult().get()));
            Promise<Computer> promise = Simulator.warehouse.down(Computer);
            promise.subscribe(() -> {
                checkAndSign(promise);
            });

        });
        AtomicInteger i = new AtomicInteger();
        Students.forEach((student -> sendMessage(gradesActionsList.get(i.getAndIncrement()), student, new StudentPrivateState())));
    }

    private void checkAndSign(Promise<Computer> promise) {
        gradesList.forEach((map -> signaturesList.add(promise.get().checkAndSign(Conditions, map))));
        Simulator.warehouse.up(promise.get().getComputerType());
        ArrayList<Action<Boolean>> actions = new ArrayList<>();
        AtomicInteger helper = new AtomicInteger();
        Students.forEach((student) -> {
            Action<Boolean> setSignature = new Action<Boolean>() {
                @Override
                protected void start() {
                    ((StudentPrivateState) this.actionState).setSignature(signaturesList.get(helper.getAndIncrement()));
                    complete(true);
                }
            };

            actions.add(setSignature);
        });
        then(actions, () ->{
            complete(true);

        });
        AtomicInteger i = new AtomicInteger();
        Students.forEach((student -> sendMessage(actions.get(i.getAndIncrement()), student, new StudentPrivateState())));
    }
}

