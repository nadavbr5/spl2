package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * represents a warehouse that holds a finite amount of computers
 *  and their suspended mutexes.
 * 
 */
public class Warehouse {
    private ConcurrentHashMap<String, SuspendingMutex> computers= new ConcurrentHashMap<>();

    public boolean addComputer(String type, long success, long fail){
        Computer computer = new Computer(type);
        computer.setFailSig(fail);
        computer.setSuccessSig(success);
        SuspendingMutex suspendingMutex= new SuspendingMutex(computer);
        return this.computers.put(type,suspendingMutex) == null;
    }

    public Promise<Computer> checkAndSign(String computerType){
         return computers.get(computerType).down();
    }
}
