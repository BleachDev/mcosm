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

	public static final List<Block> SOLID = Arrays.asList(
			Blocks.GRASS, Blocks.STONE, Blocks.DIRT, Blocks.COAL_ORE, Blocks.IRON_ORE, Blocks.GOLD_ORE, Blocks.LAPIS_ORE,
			Blocks.REDSTONE_ORE, Blocks.DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.WATER, Blocks.FLOWING_WATER,
			Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.BEDROCK);
	
	public AlignToGroundOperation(List<BlockPos> poses) {
		this.thread = new OperationThread<List<BlockPos>>() {
			public void run() {
				World world = Minecraft.getMinecraft().getIntegratedServer().getWorld(Minecraft.getMinecraft().player.dimension);
				List<BlockPos> aligned = new ArrayList<>();
				
				for (BlockPos b: poses) {
					MutableBlockPos curPos = new MutableBlockPos(b.getX(), 0, b.getZ());
					
					// (slow) Way of getting the highest block on a coordinate with cubicchunks infinite height
					int airStreak = 0;
					for (int i = 1; i < 9200; i++) {
						curPos.setY(i);
						Block bl = world.getBlockState(curPos).getBlock();
						
						if (!SOLID.contains(bl)) airStreak++;
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
