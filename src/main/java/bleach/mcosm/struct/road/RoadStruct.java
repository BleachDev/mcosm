package bleach.mcosm.struct.road;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.block.SetBlocksOperation;
import bleach.mcosm.operation.nodes.AlignToGroundOperation;
import bleach.mcosm.operation.nodes.MakeStripeLineOperation;
import bleach.mcosm.struct.Creatable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class RoadStruct extends Creatable {

	private static final List<Block> REPLACEABLE = Arrays.asList(
			Blocks.SNOW, Blocks.YELLOW_FLOWER, Blocks.RED_FLOWER, Blocks.DEADBUSH, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM,
			Blocks.BROWN_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.VINE, Blocks.WATERLILY, Blocks.LEAVES,
			Blocks.LEAVES2, Blocks.DOUBLE_PLANT, Blocks.LOG, Blocks.LOG2, Blocks.AIR, Blocks.REEDS, Blocks.TALLGRASS);
	
	public List<BlockPos> nodes;
	public LinkedHashMap<BlockPos, IBlockState> fill;
	
	protected int thick;
	protected int stripeLen;
	protected IBlockState state;
	protected IBlockState stripeState;
	
	public RoadStruct(List<BlockPos> nodes, IBlockState state, int thick, IBlockState stripe, int stripeLen) {
		this.nodes = nodes;
		this.thick = thick;
		this.state = state;
		this.stripeState = stripe;
		this.stripeLen = stripeLen;
	}
	
	public RoadStruct(List<BlockPos> nodes, IBlockState state, int thick) {
		this(nodes, state, thick, state, 0);
	}

	public Operation getOperation(int op) {
		switch (op) {
			case 0: return new MakeStripeLineOperation(nodes, state, stripeState, stripeLen, thick);
			case 1: return new AlignToGroundOperation(getFillPoses());
			case 2: return new SetBlocksOperation(
					fill.entrySet().stream().filter(e -> e.getValue() == state).map(e -> e.getKey()).collect(Collectors.toList()), state);
			case 3: return new SetBlocksOperation(
					fill.entrySet().stream().filter(e -> e.getValue() == stripeState).map(e -> e.getKey()).collect(Collectors.toList()), stripeState);
		}
		
		return null;
	}

	public int getOpCount() {
		return 4;
	}

	@SuppressWarnings("unchecked")
	public void onResult(int op, Object returnVal) {
		switch (op) {
			case 0:
				fill = (LinkedHashMap<BlockPos, IBlockState>) returnVal;
				break;
			case 1:
				List<BlockPos> fillList = (List<BlockPos>) returnVal;
				int i = 0;
				for (Entry<BlockPos, IBlockState> e: new LinkedHashMap<>(fill).entrySet()) {
					fill.remove(e.getKey());
					
					if (!REPLACEABLE.contains(Minecraft.getMinecraft().world.getBlockState(fillList.get(i)).getBlock())
							&& REPLACEABLE.contains(Minecraft.getMinecraft().world.getBlockState(fillList.get(i).up()).getBlock())) {
						fill.put(fillList.get(i), e.getValue());
					} else {
						fill.put(fillList.get(i).down(), e.getValue());
						if (Minecraft.getMinecraft().world.getBlockState(fillList.get(i)).getBlock() != Blocks.AIR) {
						}
					}
					i++;
				}
				
				break;
		}
	}
	
	private List<BlockPos> getFillPoses() {
		return fill.entrySet().stream().map(e -> e.getKey()).collect(Collectors.toList());
	}
}
