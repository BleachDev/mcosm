package bleach.mcosm.struct;

import java.util.ArrayList;
import java.util.List;

import bleach.mcosm.operation.Operation;

public abstract class Creatable {

	protected List<Operation> operations = new ArrayList<>();
	
	public String progress = "";
	
	public boolean tick() {
		int c = getOpCount() - 1;
		if (c < 0) return true;
		
		if (operations.isEmpty())  {
			operations.add(getOperation(0));
			operations.get(0).tick();
			
			Operation op = operations.get(0);
			progress = String.format("[%d/%d] %s: %s%% %s",
					1, c + 1, op.getClass().getSimpleName().replace("Operation", ""), (Math.round(op.getProgress() * 10000) / 100d),
					(op.isSleeping() ? "\u00a7c(" + op.getSleepReason() + ")" : ""));
		} else {
			int cur = operations.size() - 1;
			Operation op = operations.get(cur);
			
			progress = String.format("[%d/%d] %s: %s%% %s",
					cur + 1, c + 1, op.getClass().getSimpleName().replace("Operation", ""), (Math.round(op.getProgress() * 10000) / 100d),
					(op.isSleeping() ? "\u00a7c(" + op.getSleepReason() + ")" : ""));
			
			if (op.tick()) {
				if (cur == c) {
					onResult(cur, op.getResult());
					return true;
				} else {
					onResult(cur, op.getResult());
					operations.add(getOperation(cur + 1));
				}
			}
		}
		
		return false;
	}
	
	public abstract Operation getOperation(int op);
	public abstract int getOpCount();
	public abstract void onResult(int op, Object returnVal);
	
	public void stop() {
		for (Operation op: operations) {
			if (op != null) op.kill();
		}
	}
}
