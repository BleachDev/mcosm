package bleach.mcosm.struct.natural;

import java.util.Collections;
import java.util.List;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.natural.GenTreeOperation;
import bleach.mcosm.operation.nodes.AlignToGroundOperation;
import bleach.mcosm.struct.Creatable;
import bleach.mcosm.utils.gen.TreeType;
import net.minecraft.util.math.BlockPos;

public class TreeStruct extends Creatable {

	public TreeType type;
	public BlockPos pos;

	public TreeStruct(BlockPos pos) {
		this(pos, TreeType.BIG_OAK);
	}

	public TreeStruct(BlockPos pos, TreeType type) {
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
