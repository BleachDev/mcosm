package bleach.mcosm.operation;

import java.util.Random;

public class OperationThread<T> extends Thread {
	private volatile T output;
	private volatile double progress = 0;
	
	private volatile boolean isSleeping = false;
	private volatile String sleepReason = "";
	
	private static Random randName = new Random();
	
	public OperationThread() {
		super("OSMThread-" + randName.nextInt());
	}
	
	public OperationThread(Runnable runnable) {
		super(null, runnable, "OSMThread-" + randName.nextInt());
	}
	
	public T getOutput() {
		return output;
	}
	
	public void setOutput(T input) {
		output = input;
	}
	
	public double getProgress() {
		return progress;
	}
	
	public void setProgress(double prog) {
		progress = prog;
	}
	
	public boolean isSleeping() {
		return isSleeping;
	}
	
	public void setSleeping(boolean bool) {
		isSleeping = bool;
	}
	
	public String getSleepReason() {
		return sleepReason;
	}
	
	public void setSleepReason(String reason) {
		sleepReason = reason;
	}
}
