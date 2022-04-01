package bleach.mcosm.struct.building;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.block.AddWindowOperation;
import bleach.mcosm.operation.block.SetBlocksOperation;
import bleach.mcosm.operation.block.StretchBlocksOperation;
import bleach.mcosm.operation.nodes.AlignToGroundOperation;
import bleach.mcosm.operation.nodes.MakeFillOperation;
import bleach.mcosm.operation.nodes.MakeOutlineOperation;
import bleach.mcosm.struct.Creatable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class BuildingStruct extends Creatable {

	public List<BlockPos> nodes;
	public List<BlockPos> outline;
	public List<BlockPos> fill;

	public IBlockState state;
	public int height;

	protected int floors = 1;
	protected IBlockState roofState = null;
	protected IBlockState windowState = null;

	public BuildingStruct(List<BlockPos> nodes, IBlockState state, int height) {
		this.nodes = nodes;
		this.height = height;
		this.state = state;
	}

	public BuildingStruct(List<BlockPos> nodes, IBlockState state, IBlockState windowState, int height, int floors) {
		this(nodes, state, height);
		this.floors = floors;
		this.windowState = windowState;
	}

	public BuildingStruct(List<BlockPos> nodes, IBlockState state, IBlockState windowState, IBlockState roofState, int height, int floors) {
		this(nodes, state, windowState, height, floors);
		this.roofState = roofState;
	}

	public Operation getOperation(int op) {
		switch (op) {
			case 0: return new MakeOutlineOperation(nodes, false);
			case 1: return new MakeFillOperation(outline);
			case 2: return new AlignToGroundOperation(fill);
			case 3: return new SetBlocksOperation(fill, state);
			case 4: return new StretchBlocksOperation(fill, height);
			case 5:
				if (windowState != null) {
					return new AddWindowOperation(outline, windowState, height, floors);
				} else {
					return new SetBlocksOperation(fill.stream().map(b -> b.up(height - 1)).collect(Collectors.toList()), roofState);
				}
			case 6: return new SetBlocksOperation(fill.stream().map(b -> b.up(height - 1)).collect(Collectors.toList()), roofState);
		}

		return null;
	}

	public int getOpCount() {
		return 5 + (windowState != null ? 1 : 0) + (roofState != null ? 1 : 0);
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
				
				outline = outline.stream().map(b -> {
					Optional<BlockPos> bp = fill.stream().filter(b1 -> b1.getX() == b.getX() && b1.getZ() == b.getZ()).findFirst();
					return bp.isPresent() ? bp.get() : b;
				}).collect(Collectors.toList());
				
				break;
		}
	}
}
