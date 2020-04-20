package bleach.mcosm.struct;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.misc.GenTreeOperation;
import net.minecraft.block.BlockPlanks;
import net.minecraft.util.math.BlockPos;

public class TreeStruct extends Creatable {

	public BlockPlanks.EnumType type;
	public BlockPos pos;
	
	public TreeStruct(BlockPos pos) {
		this(pos, BlockPlanks.EnumType.OAK);
	}
	
	public TreeStruct(BlockPos pos, BlockPlanks.EnumType type) {
		this.type = type;
		this.pos = pos;
	}
	
	public Operation getOperation(int op) {
		return new GenTreeOperation(pos, type);
	}

	public int getOpCount() {
		return 1;
	}

	public void onResult(int op, Object returnVal) {
	}

}
