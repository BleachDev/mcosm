package bleach.mcosm.operation.natural;

import java.util.List;

import bleach.mcosm.operation.OperationThread;
import bleach.mcosm.utils.gen.TreeType;
import net.minecraft.util.math.BlockPos;

public class GenTreeRowOperation extends GenTreeOperation {

	public GenTreeRowOperation(List<BlockPos> poses, int seperation) {
		this(poses, TreeType.BIG_OAK, seperation);
	}
	
	public GenTreeRowOperation(List<BlockPos> poses, TreeType type, int seperation) {
		super();
		
		this.thread = new OperationThread<Void>() {
			public void run() {
				for (int i = 0; i < poses.size(); i++) {
					if (i % (seperation + 1) != 0) continue;
					
					generateTree(type, poses.get(i));
					setProgress(i / (double) poses.size());
				}
			}
		};
	}
}
