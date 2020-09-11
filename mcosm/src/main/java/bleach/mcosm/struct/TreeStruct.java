package bleach.mcosm.struct;

import java.util.Collections;
import java.util.List;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.misc.GenTreeOperation;
import bleach.mcosm.operation.nodes.AlignToGroundOperation;
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
		switch(op) {
			case 0: return new AlignToGroundOperation(Collections.singletonList(pos));
			case 1: return new GenTreeOperation(pos, type);
		}
		
		return null;
	}

	public int getOpCount() {
		return 2;
	}

	@SuppressWarnings("unchecked")
	public void onResult(int op, Object returnVal) {
		if (op == 0) {
			pos = ((List<BlockPos>) returnVal).get(0);
		}
	}

}
