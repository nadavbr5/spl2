package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * <p>
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 */
public class SuspendingMutex {
    private Computer computer;
    private AtomicBoolean lock;
    private ConcurrentLinkedQueue<Promise<Computer>> promises;

    /**
     * Constructor
     *
     * @param computer
     */
    public SuspendingMutex(Computer computer) {
        lock = new AtomicBoolean();
        promises = new ConcurrentLinkedQueue<>();
        this.computer = computer;
    }

    /**
     * Computer acquisition procedure
     * Note that this procedure is non-blocking and should return immediatly
     * <p>
     * computer's type
     *
     * @return a promise for the requested computer
     */
    public Promise<Computer> down() {
        Promise<Computer> promise = new Promise<>();
        if (lock.compareAndSet(false, true)) {
            promise.resolve(computer);
        }
        else
            this.promises.add(promise);
        return promise;
    }

    /**
     * Computer return procedure
     * releases a computer which becomes available in the warehouse upon completion
     */
    public void up() {
        while (!this.promises.isEmpty()) {
            promises.poll().resolve(computer);
        }
        lock.compareAndSet(true, false);
    }
}
