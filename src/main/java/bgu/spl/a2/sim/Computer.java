package bgu.spl.a2.sim;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Computer {

	private String computerType;
	private long failSig;
	private long successSig;

	public Computer(String computerType) {
		this.computerType = computerType;
	}

	/**
	 * this method checks if the courses' grades fulfill the conditions
	 * @param courses
	 * 							courses that should be pass
	 * @param coursesGrades
	 * 							courses' grade
	 * @return a signature if couersesGrades grades meet the conditions
	 */
	public long checkAndSign(List<String> courses, Map<String, Integer> coursesGrades){
		AtomicBoolean ans = new AtomicBoolean(true);
		courses.forEach((course)-> {
			 if(!(coursesGrades.get(course)!=null &&coursesGrades.get(course)>=56))
			ans.compareAndSet(true,false);
		});

		return (ans.get() ? successSig : failSig);
	}

	public void setFailSig(long failSig) {
		this.failSig = failSig;
	}

	public void setSuccessSig(long successSig) {
		this.successSig = successSig;
	}

	public String getComputerType() {
		return computerType;
	}
}
