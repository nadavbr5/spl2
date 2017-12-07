package bgu.spl.a2.sim.actions;


import bgu.spl.a2.Action;

import java.util.ArrayList;

/**
 * @author nadav.
 *
 */
public class CloseCourseAction extends Action<Boolean> {
    private final String course;
    private final String department;

    public CloseCourseAction(String course, String department) {
        this.course = course;
        this.department = department;
    }
    @Override
    protected void start() {
        ArrayList<Action<Boolean>> actions= new ArrayList<>();

    }
}
