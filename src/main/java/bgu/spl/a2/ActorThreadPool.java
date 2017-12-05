package bgu.spl.a2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
    private ConcurrentHashMap<String, Queue<Action>> actorsActions;
    private HashMap<String, PrivateState> actorsPrivateStates;
    private ConcurrentHashMap<String, AtomicBoolean> actorsLocks;
    private ArrayList<Thread> threads;
    private VersionMonitor monitor;


    /**
     * creates a {@link ActorThreadPool} which has nthreads. Note, threads
     * should not get started until calling to the {@link #start()} method.
     * <p>
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this thread
     *                 pool
     */
    public ActorThreadPool(int nthreads) {
        actorsActions = new ConcurrentHashMap<>();
        actorsPrivateStates = new HashMap<>();
        actorsLocks = new ConcurrentHashMap<>();
        monitor = new VersionMonitor();
        threads = new ArrayList<>();
        for (int i = 0; i < nthreads; ++i) {
            threads.add(new Thread(this::eventLoop));
        }
    }

    /**
     * submits an action into an actor to be executed by a thread belongs to
     * this thread pool
     *
     * @param action     the action to execute
     * @param actorId    corresponding actor's id
     * @param actorState actor's private state (actor's information)
     */
    public void submit(Action<?> action, String actorId, PrivateState actorState) {
        // TODO: replace method body with real implementation
        throw new UnsupportedOperationException("Not Implemented Yet.");
    }

    /**
     * closes the thread pool - this method interrupts all the threads and waits
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     * <p>
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is interrupted
     */
    public void shutdown() throws InterruptedException {
        // TODO: replace method body with real implementation
        throw new UnsupportedOperationException("Not Implemented Yet.");
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        threads.forEach(Thread::start);
    }

    /**
     * create the loop for this thread to wait until
     * there is an unlocked actor with at least one action.
     */
    private void eventLoop() {
        while (true) {
            findUnlockedActor();
            try {
                monitor.await(monitor.getVersion());
            } catch (InterruptedException e) {
                //todo: do something when interrupted
                return;
            }
        }

    }

    /**
     * searches for an unlocked actor
     * meaning an actor which no thread is working on and has at least one action in his queue).
     * if found one-the function locks the actor and fetches an action.
     */
    private void findUnlockedActor() {
        String actorId = actorsLocks.search(actorsLocks.size(), ((id, lock) -> {
            if (!lock.compareAndSet(false, true)) {
                return null;
            }
            return id;

        }));
        if (actorId == null) {
            return;
        }
        //todo: add function
        monitor.inc();
//        actorsLocks.forEach(((id, lock) -> {
//            if (lock.compareAndSet(false, true)) {
//                todo: add function
//                monitor.inc();
//            }


//        }));
    }

}
