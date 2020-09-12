package bleach.mcosm.struct.natural;

import java.util.List;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.natural.GenTreeRowOperation;
import bleach.mcosm.operation.nodes.AlignToGroundOperation;
import bleach.mcosm.operation.nodes.MakeLineOperation;
import bleach.mcosm.struct.Creatable;
import bleach.mcosm.utils.gen.TreeType;
import net.minecraft.util.math.BlockPos;

public class TreeRowStruct extends Creatable {

	public TreeType type;
	public List<BlockPos> poses;
	public List<BlockPos> nodes;
	public int seperation;

	public TreeRowStruct(List<BlockPos> nodes, TreeType type, int seperation) {
		this.type = type;
		this.nodes = nodes;
		this.seperation = seperation;
	}
	
	public Operation getOperation(int op) {
		switch(op) {
			case 0: return new MakeLineOperation(nodes, 0);
			case 1: return new AlignToGroundOperation(poses);
			case 2: return new GenTreeRowOperation(poses, type, seperation);
		}
		
		return null;
	}

	public int getOpCount() {
		return 3;
	}

	@SuppressWarnings("unchecked")
	public void onResult(int op, Object returnVal) {
		if (op <= 1) {
			poses = (List<BlockPos>) returnVal;
		}
	}
}
