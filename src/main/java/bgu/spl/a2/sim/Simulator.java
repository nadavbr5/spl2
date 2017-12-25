
package bgu.spl.a2.sim;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import bgu.spl.a2.Action;
import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
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
        count = new CountDownLatch(manager.Phase2.size());
        phase(manager.Phase2);
        count = new CountDownLatch(manager.Phase3.size());
        phase(manager.Phase3);
        HashMap<String, PrivateState> SimulationResult;
        SimulationResult = Simulator.end();
        try (FileOutputStream fout= new FileOutputStream("result.ser");
             ObjectOutputStream oos = new ObjectOutputStream(fout)){
            oos.writeObject(SimulationResult);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void phase(ArrayList<JsonObject> phase) {
        phase.forEach((jsonObject) -> {
            Action action;
            PrivateState privateState = null;
            String actor = "";
            switch (jsonObject.get("Action").getAsString()) {
                case "Open Course": {
                    actor = jsonObject.get("Department").getAsString();
                    action = gson.fromJson(jsonObject, OpenANewCourseAction.class);
                    privateState=new DepartmentPrivateState();
                    break;
                }
                case "Add Student": {
                    actor = jsonObject.get("Department").getAsString();
                    action =gson.fromJson(jsonObject,AddStudent.class);
                    privateState=new DepartmentPrivateState();
                    break;
                }
                case "Participate In Course": {
                    actor = jsonObject.get("Course").getAsString();
                    action = gson.fromJson(jsonObject, ParticipatingInCourseAction.class);
                    privateState=new CoursePrivateState();
                    break;
                }
                case "Add Spaces": {
                    actor = jsonObject.get("Course").getAsString();
                    action = gson.fromJson(jsonObject, AddSpaces.class);
                    privateState=new CoursePrivateState();
                    break;
                }
                case "Register With Preferences": {
                    actor = jsonObject.get("Student").getAsString();
                    action = gson.fromJson(jsonObject,RegisterWithPreferences.class);
                    privateState=new StudentPrivateState();
                    break;
                }
                case "Unregister": {
                    actor = jsonObject.get("Course").getAsString();
                    action = gson.fromJson(jsonObject, UnregisterAction.class);
                    privateState = new CoursePrivateState();
                    break;
                }
                case "Close Course": {
                    action= gson.fromJson(jsonObject, CloseCourseAction.class);
                    actor=jsonObject.get("Department").getAsString();
                    privateState=new DepartmentPrivateState();
                    break;
                }
                case "Administrative Check": {
                    actor = jsonObject.get("Department").getAsString();
                     action = gson.fromJson(jsonObject, CheckAdministrativeObligationAction.class);
                     privateState=new DepartmentPrivateState();
                    break;
                }default:action=new EmptyAction();

            }
            action.getResult().subscribe(() -> {
                count.countDown();
            });
            actorThreadPool.submit(action, actor, privateState);
        });
        try {
            count.await();
        } catch (InterruptedException e) {
        }


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
        try {
            actorThreadPool.shutdown();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return actorThreadPool.getPrivateStates();
    }

    public static void main(String[] args) {
        //todo change back to int
        gson = new Gson();
        try( JsonReader reader=new JsonReader(new FileReader(args[0]))) {
            manager = gson.fromJson(reader, Manager.class);
            attachActorThreadPool(new ActorThreadPool(manager.threads));
            warehouse = new Warehouse();
            start();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
    }
}
