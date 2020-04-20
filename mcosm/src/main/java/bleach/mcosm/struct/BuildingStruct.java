package bleach.mcosm.struct;

import java.util.List;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.block.SetBlocksOperation;
import bleach.mcosm.operation.block.StretchBlocksOperation;
import bleach.mcosm.operation.nodes.AlignToGroundOperation;
import bleach.mcosm.operation.nodes.MakeFillOperation;
import bleach.mcosm.operation.nodes.MakeOutlineOperation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BuildingStruct extends Creatable {

	public List<BlockPos> nodes;
	public List<BlockPos> outline;
	public List<BlockPos> fill;
	
	public IBlockState state;
	public int height;
	
	public BuildingStruct(List<BlockPos> nodes, IBlockState state, int height) {
		this.nodes = nodes;
		this.height = height;
		this.state = state;
	}

	public Operation getOperation(int op) {
		switch (op) {
			case 0: return new MakeOutlineOperation(nodes, false);
			case 1: return new MakeFillOperation(outline);
			case 2: return new AlignToGroundOperation(fill);
			case 3: return new AlignToGroundOperation(outline);
			case 4: return new SetBlocksOperation(fill, state);
			case 5: return new StretchBlocksOperation(fill, height);
		}
		
		return null;
	}

	public int getOpCount() {
		return 6;
	}

	@SuppressWarnings("unchecked")
	public void onResult(int op, Object returnVal) {
		switch (op) {
			case 0:
				outline = (List<BlockPos>) returnVal;
				break;
			case 1:
				fill = (List<BlockPos>) returnVal;
				break;
			case 2:
				fill = (List<BlockPos>) returnVal;
				break;
			case 3:
				outline = (List<BlockPos>) returnVal;
				break;
		}
	}
}
