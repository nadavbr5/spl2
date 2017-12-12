/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.Promise;
import bgu.spl.a2.sim.actions.OpenANewCourseAction;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {


    public static ActorThreadPool actorThreadPool;
    public static Warehouse warehouse;
    private static Manager manager;
    private static Gson gson;
    private static CountDownLatch count;

    /**
     * Begin the simulation Should not be called before attachActorThreadPool()
     */
    public static void start() {
        manager.Computers.forEach((jsonObject -> {
            warehouse.addComputer(jsonObject.get("Type").getAsString(),
                    jsonObject.get("Sig Success").getAsLong(),
                    jsonObject.get("Sig Fail").getAsLong());
        }));
        actorThreadPool.start();
        count = new CountDownLatch(manager.Phase1.size());
        phase(manager.Phase1);



    }

    private static void phase(ArrayList<JsonObject> phase) {
        phase.forEach((jsonObject)->{
            switch (jsonObject.get("Action").getAsString()) {
                case "Open Course": {
                    openCourse(jsonObject);
                    break;
                }
                case "Add Student":{
                    addStudent(jsonObject);
                    break;
                }
                case "Participate In Course":{
                    participateInCourse(jsonObject);
                    break;
                }
                case "Add Spaces":{
                    addSpaces(jsonObject);
                    break;
                }
                case "Register With Preferences":{
                    registerWithPereferences(jsonObject);
                    break;
                }
                case "Unregister":{
                    unregister(jsonObject);
                    break;
                }
                case "Close Course":{
                    closeCourse(jsonObject);
                    break;
                }
                case "Administrative Check":{
                    administrativeCheck(jsonObject);
                    break;
                }

            }

        });



    }

    private static void addStudent(JsonObject jsonObject) {

    }

    private static void openCourse(JsonObject jsonObject) {
        Action action;
        String name =jsonObject.get("Course").getAsString();
        String department =jsonObject.get("Department").getAsString();
        int space = jsonObject.get("Space").getAsInt();
        List prerequisites = gson.fromJson(jsonObject.get("Prerequisites"), List.class);
        action = new OpenANewCourseAction(name, space, (List<String>) prerequisites);
       actorThreadPool.submit(action,department,new DepartmentPrivateState());
       action.getResult().subscribe(()->count.countDown());
    }

    /**
     * attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
     *
     * @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
     */
    public static void attachActorThreadPool(ActorThreadPool myActorThreadPool) {
        Simulator.actorThreadPool = myActorThreadPool;
    }

    /**
     * shutdown the simulator
     * returns list of privateStates
     *
     * @return
     */
    public static HashMap<String, PrivateState> end() {
        //TODO: replace method body with real implementation
        throw new UnsupportedOperationException("Not Implemented Yet.");
    }

    public static int main(String[] args) {
        gson = new Gson();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(args[0]));
            manager = gson.fromJson(reader, Manager.class);
            attachActorThreadPool(new ActorThreadPool(manager.threads));
            warehouse = new Warehouse();
            start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static class Manager {
        private int threads;
        private ArrayList<JsonObject> Computers;
        @SerializedName("Phase 1")
        private ArrayList<JsonObject> Phase1;
        @SerializedName("Phase 2")
        private ArrayList<JsonObject> Phase2;
        @SerializedName("Phase 3")
        private ArrayList<JsonObject> Phase3;

        public Manager() {
            this.Computers = new ArrayList<>();
            this.Phase1 = new ArrayList<>();
            this.Phase2 = new ArrayList<>();
            this.Phase3 = new ArrayList<>();
        }
    }
}
