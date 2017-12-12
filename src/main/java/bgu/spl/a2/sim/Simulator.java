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
        Action action;
        switch (manager.Phase1.get(0).get("Action").getAsString()) {
            case "Open Course": {
                openCourse();
            }
        }


    }

    private static Action<?> openCourse() {
        Action action;
        String name = manager.Phase1.get(0).get("Course").getAsString();
        String department = manager.Phase1.get(0).get("Department").getAsString();
        int space = manager.Phase1.get(0).get("Space").getAsInt();
        List prerequisites = gson.fromJson(manager.Phase1.get(0).get("Prerequisites"), List.class);
        action = new OpenANewCourseAction(name, space, (List<String>) prerequisites);
        return action;
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
