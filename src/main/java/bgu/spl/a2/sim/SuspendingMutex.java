package bgu.spl.a2.sim;
import bgu.spl.a2.Promise;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * 
 * Note: this class can be implemented without any synchronization. 
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 *
 */
public class SuspendingMutex {
	private AtomicBoolean isLocked=new AtomicBoolean();
	
	
	/**
	 * Computer acquisition procedure
	 * Note that this procedure is non-blocking and should return immediatly
	 * 
	 * @param computerType
	 * 					computer's type
	 * 
	 * @return a promise for the requested computer
	 */
	public Promise<Computer> down(){
		if (isLocked.compareAndSet(false, true)) {

		} else {

		}
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}
	/**
	 * Computer return procedure
	 * releases a computer which becomes available in the warehouse upon completion
	 * 
	 * @param computer
	 */
	public void up(){
		//TODO: replace method body with real implementation
		throw new UnsupportedOperationException("Not Implemented Yet.");
	}
}
