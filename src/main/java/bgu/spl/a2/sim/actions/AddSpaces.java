package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;

import java.lang.management.BufferPoolMXBean;

/**
 * this class is in actor of course
 */

public class AddSpaces extends Action<Boolean>{
    private final Integer availableSpots;

    public AddSpaces(int availableSpots){
        this.availableSpots=availableSpots;
    }

    @Override
    protected void start() {
        Integer newAvailableSpots = ((CoursePrivateState) this.actionState).getAvailableSpots() + availableSpots;
        ((CoursePrivateState) this.actionState).setAvailableSpots(newAvailableSpots);
        complete(true);
    }
}
