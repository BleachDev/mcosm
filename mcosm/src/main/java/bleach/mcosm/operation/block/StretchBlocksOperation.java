package bleach.mcosm.operation.block;

import java.util.ArrayList;
import java.util.List;

import bleach.mcosm.operation.OperationThread;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class StretchBlocksOperation extends AbstractBlockOperation {
	
	private List<ChunkPos> chunks = new ArrayList<>();
	private List<ChunkPos> reloaded = new ArrayList<>();
	private int reloadH = 1;
	
	public StretchBlocksOperation(List<BlockPos> poses, int height) {
		if (poses.isEmpty()) return;
		
		for (BlockPos b: poses) {
			ChunkPos c = new ChunkPos(b);
			if (!chunks.contains(c)) chunks.add(c);
			height = Math.min(height, 255 - b.getY());
		}
		
		int realHeight = height;
		
		this.thread = new OperationThread<Void>() {
			
			public void run() {
				if (poses.isEmpty() || realHeight == 0) return;
				
				World world = Minecraft.getMinecraft().getIntegratedServer().getWorld(Minecraft.getMinecraft().player.dimension);
				
				double i = 0;
				for (BlockPos b: poses) {
					IBlockState state = world.getBlockState(b);
					for (int h = 1; h < realHeight; h++) {
						setBlock(b.up(h), world, state);
						setProgress((i / (double) (poses.size() * (realHeight - 1) - 1)) / 2);
						i += 0.5;
					}
				}
				
				World clWorld = Minecraft.getMinecraft().world;
				while (reloaded.size() < chunks.size()) {
					ChunkPos cp = chunks.get(reloaded.size());
					int newReload = Math.min(3, realHeight - reloadH);
					 
					for (BlockPos b: poses) {
						if (b.getX() >> 4 == cp.x && b.getZ() >> 4 == cp.z) {
							for (int h = reloadH; h <= reloadH + newReload; h++) {
								BlockPos nb = b.up(h);
								validateBlock(nb, world, clWorld);
							}
						}
					}
					
					reloadH += newReload + 1;
					if (reloadH == realHeight + 1) {
						reloaded.add(cp);
						reloadH = 1;
					}
					
					setProgress(0.5 + ((double) reloaded.size() / (double) chunks.size()) / 2d);
				}
			}
		};
	}
}
