package bleach.mcosm.operation;

public abstract class Operation {

	protected OperationThread<?> thread = new OperationThread<>();
	
	protected boolean started = false;
	
	public void start() {
		if (!started) {
			if (useMainThread()) thread.run();
			else thread.start();
			started = true;
		}
	}
	
	/** Returns true if the operation is done **/
	public boolean tick() {
		if (thread.isSleeping()) {
			return false;
		} if (started) {
			return !thread.isAlive();
		} else {
			start();
			return false;
		}
	}
	
	public Object getResult() {
		return thread.getOutput();
	}
	
	public double getProgress() {
		return thread.getProgress();
	}
	
	public boolean isSleeping() {
		return thread.isSleeping();
	}
	
	public String getSleepReason() {
		return thread.getSleepReason();
	}
	
	@SuppressWarnings("deprecation")
	public void kill() {
		thread.stop(); // yikes
	}
	
	protected abstract boolean useMainThread();
}
