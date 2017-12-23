/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import bgu.spl.a2.sim.actions.*;
import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import bgu.spl.a2.sim.privateStates.DepartmentPrivateState;
import bgu.spl.a2.sim.privateStates.StudentPrivateState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
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
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream("result.ser");
            oos = new ObjectOutputStream(fout);
            oos.writeObject(SimulationResult);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fout != null)
                    fout.close();
                if (oos != null)
                    oos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void phase(ArrayList<JsonObject> phase) {
        phase.forEach((jsonObject) -> {
            switch (jsonObject.get("Action").getAsString()) {
                case "Open Course": {
                    openCourse(jsonObject);
                    break;
                }
                case "Add Student": {
                    addStudent(jsonObject);
                    break;
                }
                case "Participate In Course": {
                    participateInCourse(jsonObject);
                    break;
                }
                case "Add Spaces": {
                    addSpaces(jsonObject);
                    break;
                }
                case "Register With Preferences": {
                    registerWithPreferences(jsonObject);
                    break;
                }
                case "Unregister": {
                    unregister(jsonObject);
                    break;
                }
                case "Close Course": {
                    closeCourse(jsonObject);
                    break;
                }
                case "Administrative Check": {
                    administrativeCheck(jsonObject);
                    break;
                }

            }

        });
        try {
            count.await(20,TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }


    }

    private static void administrativeCheck(JsonObject jsonObject) {
        String department = jsonObject.get("Department").getAsString();
        List students = gson.fromJson(jsonObject.get("Students"), List.class);
        String computerType = jsonObject.get("Computer").getAsString();
        List conditions = gson.fromJson(jsonObject.get("Conditions"), List.class);
        CheckAdministrativeObligationAction action = new CheckAdministrativeObligationAction(students, computerType, conditions);
        action.getResult().subscribe(() -> {
            count.countDown();
        });
        actorThreadPool.submit(action, department, new DepartmentPrivateState());
    }

    private static void closeCourse(JsonObject jsonObject) {
        String course = jsonObject.get("Course").getAsString();
        String department = jsonObject.get("Department").getAsString();
        CloseCourseAction action = new CloseCourseAction(course, department);
        action.getResult().subscribe(() -> {
            count.countDown();
        });
        actorThreadPool.submit(action, department, new DepartmentPrivateState());
    }

    private static void unregister(JsonObject jsonObject) {
        String student = jsonObject.get("Student").getAsString();
        String course = jsonObject.get("Course").getAsString();
        UnregisterAction action = new UnregisterAction(student, course);
        action.getResult().subscribe(() -> {
            count.countDown();
        });
        actorThreadPool.submit(action, course, new CoursePrivateState());
    }

    private static void registerWithPreferences(JsonObject jsonObject) {
        String student = jsonObject.get("Student").getAsString();
        List preferences = gson.fromJson(jsonObject.get("Preferences"), List.class);
        Type type = new TypeToken<ArrayList<Integer>>() {
        }.getType();
        List<Integer> grades = gson.fromJson(jsonObject.get("Grade"), type);
        RegisterWithPreferences action = new RegisterWithPreferences(student, preferences, grades);
        action.getResult().subscribe(() -> {
            count.countDown();
        });
        actorThreadPool.submit(action, student, new StudentPrivateState());
    }

    private static void addSpaces(JsonObject jsonObject) {
        String course = jsonObject.get("Course").getAsString();
        Integer number = jsonObject.get("Number").getAsInt();
        AddSpaces action = new AddSpaces(number);
        action.getResult().subscribe(() -> {
            count.countDown();
        });
        actorThreadPool.submit(action, course, new CoursePrivateState());
    }

    private static void participateInCourse(JsonObject jsonObject) {
        String student = jsonObject.get("Student").getAsString();
        String course = jsonObject.get("Course").getAsString();
        List<String> grades = gson.fromJson(jsonObject.get("Grade"), List.class);
        Integer grade = (grades.get(0).equals("-") ? 0 : Integer.parseInt(grades.get(0)));
        ParticipatingInCourseAction participating = new ParticipatingInCourseAction(student, grade);
        actorThreadPool.submit(participating, course, new CoursePrivateState());
        participating.getResult().subscribe(() -> {
            count.countDown();
        });

    }

    private static void addStudent(JsonObject jsonObject) {
        String department = jsonObject.get("Department").getAsString();
        String studentName = jsonObject.get("Student").getAsString();
        AddStudent action = new AddStudent(studentName);
        actorThreadPool.submit(action, department, new DepartmentPrivateState());
        action.getResult().subscribe(() -> {
            count.countDown();
        });
    }

    private static void openCourse(JsonObject jsonObject) {

        String course = jsonObject.get("Course").getAsString();
        String department = jsonObject.get("Department").getAsString();
        int space = jsonObject.get("Space").getAsInt();
        List prerequisites = gson.fromJson(jsonObject.get("Prerequisites"), List.class);
        OpenANewCourseAction action = new OpenANewCourseAction(course, space, (List<String>) prerequisites);
        actorThreadPool.submit(action, department, new DepartmentPrivateState());
        action.getResult().subscribe(() -> {count.countDown();
        });
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
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(args[0]));
            manager = gson.fromJson(reader, Manager.class);
            attachActorThreadPool(new ActorThreadPool(manager.threads));
            warehouse = new Warehouse();
            start();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//        return 0;
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
