package bleach.mcosm.struct;

import java.util.List;
import java.util.stream.Collectors;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.block.AddWindowOperation;
import bleach.mcosm.operation.block.SetBlocksOperation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class HouseStruct extends BuildingStruct {
	
	protected int floors;
	protected IBlockState roofState;
	protected IBlockState windowState;
	
	public HouseStruct(List<BlockPos> nodes, IBlockState state, IBlockState roofState, IBlockState windowState, int height, int floors) {
		super(nodes, state, height);
		this.floors = floors;
		this.roofState = roofState;
		this.windowState = windowState;
	}
	
	public Operation getOperation(int op) {
		switch (op) {
			case 6: return new AddWindowOperation(outline, windowState, height, floors);
			case 7: return new SetBlocksOperation(fill.stream().map(b -> b.up(height - 1)).collect(Collectors.toList()), roofState);
		}
		
		return super.getOperation(op);
	}

	public int getOpCount() {
		return 8;
	}
}
