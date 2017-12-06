package bgu.spl.a2.sim.privateStates;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.a2.PrivateState;

/**
 * this class describe course's private state
 */
public class CoursePrivateState extends PrivateState {


    private Integer availableSpots;
    private Integer registered;
    private List<String> regStudents;
    private List<String> prequisites;

    /**
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     */
    public CoursePrivateState() {
        this.availableSpots = 0;
        this.registered = 0;
        this.prequisites = new ArrayList<>();
        this.regStudents = new ArrayList<>();
    }

    public Integer getAvailableSpots() {
        return availableSpots;
    }

    public void setAvailableSpots(Integer availableSpots) {
        this.availableSpots = availableSpots;
    }

    public Integer getRegistered() {
        return registered;
    }

    public void setRegistered(Integer registered) {
        this.registered = registered;
    }

    public List<String> getRegStudents() {
        return regStudents;
    }

    public void setPrequisites(List<String> prequisites) {
        this.prequisites = prequisites;
    }

    public List<String> getPrequisites() {
        return prequisites;
    }

    public void registerStudent(String student) {
        regStudents.add(student);
        this.availableSpots = this.availableSpots - 1;
    }

    public void unregisterStudent(String student) {
        regStudents.remove(student);
        this.availableSpots = this.availableSpots + 1;
    }
}
