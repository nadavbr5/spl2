package bgu.spl.a2;

import bgu.spl.a2.sim.privateStates.CoursePrivateState;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
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
    public void transmissonTest() {
        ActorThreadPool pool = new ActorThreadPool(8);
        Action<String> trans = new Transmission(100, "A", "B", "bank2", "bank1");
        pool.start();
        CountDownLatch l = new CountDownLatch(1);
        pool.submit(trans, "bank1", new BankStates());
        trans.getResult().subscribe(() ->
                l.countDown());
        try {
            l.await();
            pool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //    @Test
//    public void actionExecuting() {
//        AtomicInteger integer1 = new AtomicInteger();
//        AtomicInteger integer2 = new AtomicInteger();
//        Action action1 = new Action() {
//
//            @Override
//            protected void start() {
//                integer1.incrementAndGet();
//            }
//        };
//        Action action2 = new Action() {
//
//            @Override
//            protected void start() {
//                integer2.incrementAndGet();
//            }
//        };
//        Action action3 = new Action() {
//
//            @Override
//            protected void start() {
//                integer2.incrementAndGet();
//            }
//        };
//        Action action4 = new Action() {
//
//            @Override
//            protected void start() {
//                integer2.incrementAndGet();
//            }
//        };
//        Action action5 = new Action() {
//
//            @Override
//            protected void start() {
//                integer2.incrementAndGet();
//            }
//        };
//        pool.submit(action1, "actor2", new CoursePrivateState());
//        pool.submit(action2, "actor2", new CoursePrivateState());
//        pool.submit(action3, "actor2", new CoursePrivateState());
//        pool.submit(action4, "actor2", new CoursePrivateState());
//        pool.start();
//        try {
//            pool.submit(action5, "actor2", new CoursePrivateState());
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        assertEquals(1, integer1.get());
//        assertEquals(4, integer2.get());
//        try {
//            pool.shutdown();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
    public class Transmission extends Action<String> {
        int amount;
        String sender;
        String receiver;
        String receiverBank;
        private String senderBank;

        public Transmission(int amount, String receiver, String sender,
                            String receiverBank, String senderBank) {
            this.senderBank = senderBank;
            this.sender = sender;
            this.receiver = receiver;
            this.amount = amount;
            this.receiverBank = receiverBank;
        }

        @Override
        protected void start() {
            List<Action<Boolean>> actions = new ArrayList<>();
            Action<Boolean> confAction = new Confirmation(amount, sender, receiver,
                    receiverBank, new BankStates());
            actions.add(confAction);
            sendMessage(confAction, receiverBank, new BankStates());
            then(actions, () -> {
                Boolean result = actions.get(0).getResult().get();
                if (result) {
                    complete("transmission succeed");
                    System.out.println("transmission succeed");
                } else {
                    complete("transmission failed");
                    System.out.println("transmission failed");
                }

            });
        }
    }

    public class Confirmation extends Action<Boolean> {
        private final String sender;
        private final String receiver;
        private final String receiverBank;
        private final BankStates bankStates;
        private int amount;

        public Confirmation(int amount, String sender, String receiver, String receiverBank, BankStates bankStates) {
            super();
            this.amount = amount;
            this.sender = sender;
            this.receiver = receiver;
            this.receiverBank = receiverBank;
            this.bankStates = bankStates;
        }

        @Override
        protected void start() {
            bankStates.addClient(sender);
            bankStates.addClient(receiver);
            complete(bankStates.addMoney(receiver, amount));
            System.out.println("finished confirmation");
            System.out.println(bankStates.clients.toString());
        }
    }

    public class BankStates extends PrivateState {
        ConcurrentHashMap<String, Integer> clients;

        public BankStates() {
            this.clients = new ConcurrentHashMap<>();
        }

        public void addClient(String client) {
            clients.put(client, 0);
        }

        public boolean addMoney(String client, int amount) {
            return clients.put(client, amount) == 0;
        }

    }
}
