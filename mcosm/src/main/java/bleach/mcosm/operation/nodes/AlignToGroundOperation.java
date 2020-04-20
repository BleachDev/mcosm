package bleach.mcosm.operation.nodes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.OperationThread;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class AlignToGroundOperation extends Operation {

	private static final List<Block> REPLACEABLE = Arrays.asList(
			Blocks.YELLOW_FLOWER, Blocks.DOUBLE_PLANT, Blocks.DEADBUSH, Blocks.TALLGRASS, Blocks.RED_FLOWER,
			Blocks.VINE, Blocks.LEAVES, Blocks.LEAVES2, Blocks.LOG, Blocks.LOG2, Blocks.RED_MUSHROOM,
			Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM_BLOCK, Blocks.BROWN_MUSHROOM_BLOCK, Blocks.CONCRETE);
																						// ^ Used To Prevent Stacking Buildings
																						// TODO: Permanent solution
	
	public AlignToGroundOperation(List<BlockPos> poses) {
		this.thread = new OperationThread<List<BlockPos>>() {
			public void run() {
				World world = Minecraft.getMinecraft().getIntegratedServer().getWorld(Minecraft.getMinecraft().player.dimension);
				List<BlockPos> aligned = new ArrayList<>();
				
				for (BlockPos b: poses) {
					MutableBlockPos curPos = new MutableBlockPos(b.getX(), 0, b.getZ());
					
					// (slow) Way of getting the highest block on a coordinate with cubicchunks infinite height
					int airStreak = 0;
					for (int i = 0; i < 9200; i++) {
						curPos.setY(i);
						Block bl = world.getBlockState(curPos).getBlock();
						
						if (bl == Blocks.AIR || REPLACEABLE.contains(bl)) airStreak++;
						else airStreak = 0;
						
						if (airStreak == 256) {
							break;
						}
					}
					
					aligned.add(curPos.down(255));
					setProgress((double) aligned.size() / (double) poses.size());
				}
				
				setOutput(aligned);
			}
		};
	}
	
	protected boolean useMainThread() {
		return true;
	}
}
