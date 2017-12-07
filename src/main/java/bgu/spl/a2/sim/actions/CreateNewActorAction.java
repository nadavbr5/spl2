package bgu.spl.a2.sim.actions;
import bgu.spl.a2.Action;

/**
 * this action should be in course actor if we create a new course, otherwise in student actor
 *
 */

public class CreateNewActorAction extends Action<Boolean>{
    @Override
    protected void start() {
    complete(true);
    }
}
