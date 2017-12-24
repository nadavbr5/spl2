package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

/**
 * this class is in actor of course
 */

public class AddSpaces extends Action<Boolean>{
    private Integer Number;



    @Override
    protected void start() {
        this.name="Add Spaces";
        actionState.addRecord(name);
        Integer newAvailableSpots = ((CoursePrivateState) this.actionState).getAvailableSpots() + Number;
        ((CoursePrivateState) this.actionState).setAvailableSpots(newAvailableSpots);
        complete(true);
    }
}
