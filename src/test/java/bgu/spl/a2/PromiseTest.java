package bgu.spl.a2;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;

/**
 * @author nadav.
 */
public class PromiseTest {

    @Test
    public void get() {
        Promise<Integer> promise = new Promise<>();
        try {
            promise.get();
            fail();
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void resolve() {
        Promise<Integer> promise = new Promise<>();
        try {
            promise.resolve(12);
            assertEquals(12, promise.get().intValue());
        } catch (IllegalStateException e) {
            fail();
        }
        try {
            promise.resolve(9);
            fail();
        } catch (IllegalStateException e) {
            assertEquals(12, promise.get().intValue());
        }
    }

    @Test
    public void isResolved() {
        Promise<Integer> promise = new Promise<>();
        assertEquals(false, promise.isResolved());
        try {
            promise.resolve(5);
            assertEquals(true, promise.isResolved());
        } catch (IllegalStateException e) {
            fail("got exception from function resolve");
        }
    }

    @Test
    public void subscribe() {
        Promise<Integer> promise = new Promise<>();
        AtomicBoolean added = new AtomicBoolean();
        callback call = () -> added.set(true);
        promise.subscribe(call);
        try {
            promise.resolve(5);
            assertEquals(true, added.get());
        } catch (IllegalStateException e) {
            fail("got exception from function resolve");
        }
        try {
            promise.subscribe(call);
            fail();
        } catch (IllegalStateException ignored) {
        }
    }
}