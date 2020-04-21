package bleach.mcosm.operation.block;

import java.util.ArrayList;
import java.util.List;

import bleach.mcosm.operation.OperationThread;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class AddWindowOperation extends AbstractBlockOperation {

	private List<ChunkPos> chunks = new ArrayList<>();
	private List<ChunkPos> reloaded = new ArrayList<>();
	private List<BlockPos> replaced = new ArrayList<>();
	
	private List<BlockPos> outline;
	
	public AddWindowOperation(List<BlockPos> outline, IBlockState state, int height, int floors) {
		this.outline = outline;
		
		for (BlockPos b: outline) {
			ChunkPos c = new ChunkPos(b);
			if (!chunks.contains(c)) chunks.add(c);
		}
		
		this.thread = new OperationThread<Void>() {
			
			public void run() {
				/* Abort on buildings that wouldn't make sense in the 1x1 meter minecraft grid */
				if (floors == 0 || outline.isEmpty() || height < 2 || height <= floors - 2) return;
				
				World world = Minecraft.getMinecraft().getIntegratedServer().getWorld(Minecraft.getMinecraft().player.dimension);
				
				int bound = 1;
				int windowSize = Math.round((height - bound * 2) / floors);
				
				int i = 0;
				int sinceLast = 3;
				for (BlockPos b: outline) {
					if (sinceLast >= 3 && i < outline.size() - 1 && !isEdge(b)) {
						BlockPos next = outline.get(i + 1);
						if (!isEdge(next)) {
							for (int w = 0; w < floors; w++) {
								for (int w1 = 0; w1 < windowSize - 1; w1++) {
									BlockPos b1 = b.up(bound + windowSize * w + w1), b2 = next.up(bound + windowSize * w + w1);
									setBlock(b1, world, state);
									setBlock(b2, world, state);
									replaced.add(b1);
									replaced.add(b2);
								}
							}
						}
						
						sinceLast = 0;
					}
					
					sinceLast++;
					setProgress((i / 2d) / (double) (outline.size()));
					i++;
				}
				
				World clWorld = Minecraft.getMinecraft().world;
				while (reloaded.size() < chunks.size()) {
					ChunkPos cp = chunks.get(reloaded.size());
					reloaded.add(cp);
					 
					for (BlockPos b: replaced) {
						 if (b.getX() >> 4 == cp.x && b.getZ() >> 4 == cp.z) {
							  validateBlock(b, world, clWorld);
						 }
					}
					
					setProgress(0.5 + ((double) reloaded.size() / (double) chunks.size()) / 2d);
				}
			}
		};
	}
	
	private boolean isEdge(BlockPos pos) {
		List<BlockPos> touching = new ArrayList<>();
		
		for (BlockPos b: outline) {
			if (b == pos) continue;
			
			if (Math.abs(pos.getX() - b.getX()) <= 1 && Math.abs(pos.getZ() - b.getZ()) <= 1) {
				touching.add(b);
			}
		}
		
		if (touching.size() <= 1) return true;
		if (touching.size() == 2) {
			if (Math.abs(touching.get(0).getX() - touching.get(1).getX()) + Math.abs(touching.get(0).getZ() - touching.get(1).getZ()) <= 2) return true;
		}
		if (touching.size() == 3) {
			int xpos = touching.get(0).getX();
			int zpos = touching.get(0).getZ();
			for (BlockPos b: touching) {
				if(xpos == touching.get(0).getX()) xpos = b.getX();
				if(zpos == touching.get(0).getZ()) zpos = b.getZ();
			}
			
			return xpos == touching.get(0).getX() || zpos == touching.get(0).getZ();
		}
		
		return false;
	}
}
