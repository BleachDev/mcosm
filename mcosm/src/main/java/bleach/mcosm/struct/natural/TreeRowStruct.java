package bleach.mcosm.struct.natural;

import java.util.List;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.natural.GenTreeRowOperation;
import bleach.mcosm.operation.nodes.AlignToGroundOperation;
import bleach.mcosm.operation.nodes.MakeLineOperation;
import bleach.mcosm.struct.Creatable;
import net.minecraft.block.BlockPlanks;
import net.minecraft.util.math.BlockPos;

public class TreeRowStruct extends Creatable {

	public BlockPlanks.EnumType type;
	public List<BlockPos> poses;
	public List<BlockPos> nodes;

	public TreeRowStruct(List<BlockPos> nodes) {
		this(nodes, BlockPlanks.EnumType.OAK);
	}

	public TreeRowStruct(List<BlockPos> nodes, BlockPlanks.EnumType type) {
		this.type = type;
		this.nodes = nodes;
	}
	
	public Operation getOperation(int op) {
		switch(op) {
			case 0: return new MakeLineOperation(nodes, 0);
			case 1: return new AlignToGroundOperation(poses);
			case 2: return new GenTreeRowOperation(poses);
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
