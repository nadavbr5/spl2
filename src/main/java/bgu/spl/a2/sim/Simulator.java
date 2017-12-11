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

import bgu.spl.a2.ActorThreadPool;
import bgu.spl.a2.PrivateState;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {

	
	public static ActorThreadPool actorThreadPool;
	public static Warehouse warehouse;
	
	/**
	* Begin the simulation Should not be called before attachActorThreadPool()
	*/
    public static void start(){
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
    }
	
	/**
	* attach an ActorThreadPool to the Simulator, this ActorThreadPool will be used to run the simulation
	* 
	* @param myActorThreadPool - the ActorThreadPool which will be used by the simulator
	*/
	public static void attachActorThreadPool(ActorThreadPool myActorThreadPool){
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}

	/**
	 * shutdown the simulator
	 * returns list of privateStates
	 * @return
	 */
	public static HashMap<String,PrivateState> end(){
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}
	
	public static int main(String [] args){
		Gson gson = new Gson();
		JsonReader reader = null;
		try {
			reader = new JsonReader(new FileReader(args[0]));
			Manager data = gson.fromJson(reader, Manager.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private static class Manager {
		private int threads;
		private ArrayList<JsonObject> Computers;
		private ArrayList<JsonObject> Phase1;
		private ArrayList<JsonObject> Phase2;
		private ArrayList<JsonObject> Phase3;

		public Manager() {
			this.Computers = new ArrayList<>();
			this.Phase1=new ArrayList<>();
			this.Phase2=new ArrayList<>();
			this.Phase3=new ArrayList<>();
		}
	}
}
