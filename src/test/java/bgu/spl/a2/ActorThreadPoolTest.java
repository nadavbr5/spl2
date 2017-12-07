package bgu.spl.a2;

import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * @author nadav.
 */
public class ActorThreadPoolTest {
    ActorThreadPool pool;
    ConcurrentHashMap actorActions;
    HashMap actorStates;
    ConcurrentHashMap actorLocks;
    ArrayList<Thread> threads;


    @Before
    public void setUp() throws Exception {
        pool = new ActorThreadPool(5);
        Class threadPoolClass = pool.getClass();
        try {
            Field actorsActionsField = threadPoolClass.getDeclaredField("actorsActions");
            actorsActionsField.setAccessible(true);
            actorActions = (ConcurrentHashMap) actorsActionsField.get(pool);
            Field actorsPrivateStatesField = threadPoolClass.getDeclaredField("actorsPrivateStates");
            actorsPrivateStatesField.setAccessible(true);
            actorStates = (HashMap) actorsPrivateStatesField.get(pool);
            Field actorsLocksField = threadPoolClass.getDeclaredField("actorsLocks");
            actorsLocksField.setAccessible(true);
            actorLocks = (ConcurrentHashMap) actorsLocksField.get(pool);
            Field threadsField = threadPoolClass.getDeclaredField("threads");
            threadsField.setAccessible(true);
            threads = (ArrayList<Thread>) threadsField.get(pool);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void submit() {
        Action action = new Action() {
            @Override
            protected void start() {
                int i = 5;

            }
        };
        assertEquals(0, actorActions.size());
        assertEquals(0, actorLocks.size());
        assertEquals(0, actorStates.size());
        pool.submit(action, "actor1", new CoursePrivateState());
        Queue queue = (Queue) actorActions.get("actor1");
        assertNotNull(queue);
        assertEquals(action, queue.peek());
        Action action1 = new Action() {
            @Override
            protected void start() {
                int i = 5;

            }
        };
        pool.submit(action1, "actor1", new CoursePrivateState());
        assertEquals(2, queue.size());
        assertEquals(1, actorActions.size());
        assertEquals(1, actorLocks.size());
        assertEquals(1, actorStates.size());
    }

    @Test
    public void shutdown() {
        int before = Thread.activeCount();
        pool.start();
        try {
            Thread.sleep(1000);
            pool.shutdown();
            Thread.sleep(1000);
            assertEquals(before, Thread.activeCount());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void start() {
        int before = Thread.activeCount();
        try {
            pool.start();
            Thread.sleep(1000);
            assertEquals(5, Thread.activeCount() - before);
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void actionExecuting() {
        AtomicInteger integer1 = new AtomicInteger();
        AtomicInteger integer2 = new AtomicInteger();
        Action action1 = new Action() {

            @Override
            protected void start() {
                integer1.incrementAndGet();
            }
        };
        Action action2 = new Action() {

            @Override
            protected void start() {
                integer2.incrementAndGet();
            }
        };
        Action action3 = new Action() {

            @Override
            protected void start() {
                integer2.incrementAndGet();
            }
        };
        Action action4 = new Action() {

            @Override
            protected void start() {
                integer2.incrementAndGet();
            }
        };
        Action action5 = new Action() {

            @Override
            protected void start() {
                integer2.incrementAndGet();
            }
        };
        pool.submit(action1, "actor2", new CoursePrivateState());
        pool.submit(action2, "actor2", new CoursePrivateState());
        pool.submit(action3, "actor2", new CoursePrivateState());
        pool.submit(action4, "actor2", new CoursePrivateState());
        pool.start();
        try {
            pool.submit(action5, "actor2", new CoursePrivateState());
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(1, integer1.get());
        assertEquals(4, integer2.get());
        try {
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}