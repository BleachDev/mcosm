package bleach.mcosm.operation.block;

import java.util.ArrayList;
import java.util.List;

import bleach.mcosm.operation.OperationThread;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class SetBlocksOperation extends AbstractBlockOperation {
	
	private List<ChunkPos> chunks = new ArrayList<>();
	private List<ChunkPos> reloaded = new ArrayList<>();
	
	public SetBlocksOperation(List<BlockPos> poses, IBlockState state) {
		if (poses.isEmpty()) return;
		
		for (BlockPos b: poses) {
			ChunkPos c = new ChunkPos(b);
			if (!chunks.contains(c)) chunks.add(c);
		}
		
		this.thread = new OperationThread<Void>() {
			
			public void run() {
				World world = Minecraft.getMinecraft().getIntegratedServer().getWorld(Minecraft.getMinecraft().player.dimension);
				double i = 0;
				for (BlockPos b: poses) {
					setBlock(b, world, state);
					setProgress(i / (double) (poses.size() - 1));
					i += 0.5;
				}
				
				World clWorld = Minecraft.getMinecraft().world;
				while (reloaded.size() < chunks.size()) {
					ChunkPos cp = chunks.get(reloaded.size());
					reloaded.add(cp);
					 
					for (BlockPos b: poses) {
						 if (b.getX() >> 4 == cp.x && b.getZ() >> 4 == cp.z) {
							 validateBlock(b, world, clWorld);
						 }
					}
					
					setProgress(0.5 + ((double) reloaded.size() / (double) chunks.size()) / 2d);
				}
			}
		};
	}
}
