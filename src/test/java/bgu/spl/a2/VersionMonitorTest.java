package bgu.spl.a2;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * @author nadav.
 */
public class VersionMonitorTest {

    @Test
    public void getVersion() {
        VersionMonitor vm=new VersionMonitor();
        assertEquals(0, vm.getVersion());
    }

    @Test
    public void inc() {
        VersionMonitor vm=new VersionMonitor();
        vm.inc();
        assertEquals(1, vm.getVersion());
    }

    @Test
    public void await() {
        VersionMonitor vm=new VersionMonitor();
        AtomicBoolean waited = new AtomicBoolean(false);
        Thread thread=new Thread(() -> {
            try {
                vm.await(vm.getVersion());
                waited.set(true);
            } catch (InterruptedException e) {
                e.printStackTrace();
                fail();
            }
        });
        thread.start();
        assertEquals(false,waited.get());
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        vm.inc();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail();
        }
        assertEquals(true,waited.get());

    }
}