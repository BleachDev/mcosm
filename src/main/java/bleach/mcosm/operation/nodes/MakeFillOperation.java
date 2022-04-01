package bleach.mcosm.operation.nodes;

import java.util.ArrayList;
import java.util.List;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.OperationThread;
import net.minecraft.util.math.BlockPos;

public class MakeFillOperation extends Operation {

	public MakeFillOperation(List<BlockPos> outline) {
		int miX = Integer.MAX_VALUE, miZ = Integer.MAX_VALUE, maX = Integer.MIN_VALUE, maZ = Integer.MIN_VALUE;
		
		for (BlockPos b: outline) {
			if (b.getX() < miX) { miX = b.getX(); }
			if (b.getZ() < miZ) { miZ = b.getZ(); }
			if (b.getX() > maX) { maX = b.getX(); }
			if (b.getZ() > maZ) { maZ = b.getZ(); }
		}
		
		init(outline, miX, miZ, maX, maZ);
	}
	
	public MakeFillOperation(List<BlockPos> outline, int minX, int minZ, int maxX, int maxZ) {
		init(outline, minX, minZ, maxX, maxZ);
	}
	
	public void init(List<BlockPos> outline, int minX, int minZ, int maxX, int maxZ) {
		thread = new OperationThread<List<BlockPos>>() {
			
			public void run() {
				if (outline.size() < 4) {
					setOutput(outline);
					return;
				}
				
				int y = outline.get(0).getY();
				
				//Flood the outside of the outline, then invert the result to get the fill
				setProgress(0.2);
				List<BlockPos> edges = new ArrayList<>();
				for (int x = minX; x <= maxX; x++) {
					edges.add(new BlockPos(x, y, minZ));
					edges.add(new BlockPos(x, y, maxZ));
				}
				
				for (int z = minZ + 1; z <= maxZ - 1; z++) {
					edges.add(new BlockPos(minX, y, z));
					edges.add(new BlockPos(maxX, y, z));
				}
				
				setProgress(0.4);
				List<BlockPos> flooded = new ArrayList<>();
				for (BlockPos b: edges) {
					if (!outline.contains(b)) { flooded.add(b); }
				}
				
				List<BlockPos> active = new ArrayList<>(flooded);
				
				setProgress(0.6);
				int totalSize = ((maxX + 1) - minX) * ((maxZ + 1) - minZ);
				boolean doneFlood = false;
				while (!doneFlood) {
					doneFlood = true;
					for (BlockPos b: new ArrayList<>(active)) {
						for (int[] i: new int[][]{{0,0,1},{1,0,0},{0,0,-1},{-1,0,0}}) {
							BlockPos newB = b.add(i[0], i[1], i[2]);
							
							if (newB.getX() < minX || newB.getZ() < minZ || newB.getX() > maxX || newB.getZ() > maxZ
									|| outline.contains(newB) || flooded.contains(newB) || active.contains(newB)) continue;
							
							flooded.add(newB);
							active.remove(b);
							active.add(newB);
							doneFlood = false;
						}
					}
					
					setProgress(0.6 + ((double) flooded.size() / (double) totalSize) * 0.2);
				}
				
				setProgress(0.8);
				List<BlockPos> fill = new ArrayList<>();
				for (int x = minX; x <= maxX; x++) {
					for (int z = minZ; z <= maxZ; z++) {
						BlockPos b = new BlockPos(x,y,z);
						if (!flooded.contains(b)) {
							fill.add(b);
						}
					}
					
					setProgress(0.8 + ((double) (x - minX) / (double) ((maxX + 1) - minX)) * 0.2);
				}
				
				setOutput(fill);
				setProgress(1);
			}
		};
	}

	protected boolean useMainThread() {
		return false;
	}
}
