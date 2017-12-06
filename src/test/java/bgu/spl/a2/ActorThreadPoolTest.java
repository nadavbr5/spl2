package bgu.spl.a2;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

/**
 * @author nadav.
 */
public class ActorThreadPoolTest {
    ActorThreadPool pool;
    ConcurrentHashMap actorActions;
    HashMap actorStates;
    ConcurrentHashMap actorLocks;


    @Before
    public void setUp() throws Exception {
        pool = new ActorThreadPool(5);
        Class threadPoolClass = pool.getClass();
        try {
            Field actorsActionsField = threadPoolClass.getDeclaredField("actorsActions");
            actorsActionsField.setAccessible(true);
//            actorActions = (ConcurrentHashMap) actorsActionsField.get(pool);
//            Field actorsActionsField = threadPoolClass.getDeclaredField("actorsActions");
//            actorsActionsField.setAccessible(true);
//            actorActions = (ConcurrentHashMap) actorsActionsField.get(pool);
//            Field actorsActionsField = threadPoolClass.getDeclaredField("actorsActions");
//            actorsActionsField.setAccessible(true);
//            actorActions = (ConcurrentHashMap) actorsActionsField.get(pool);
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

    }

    @Test
    public void shutdown() {
    }

    @Test
    public void start() {
    }
}