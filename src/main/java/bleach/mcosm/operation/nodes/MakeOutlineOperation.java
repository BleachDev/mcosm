package bleach.mcosm.operation.nodes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.OperationThread;
import bleach.mcosm.utils.BlockPosMatrix;
import net.minecraft.util.math.BlockPos;

public class MakeOutlineOperation extends Operation {

	public MakeOutlineOperation(List<BlockPos> nodes, boolean rotate) {
		thread = new OperationThread<List<BlockPos>>() {
			
			public void run() {
				if (nodes.size() == 0) {
					setOutput(nodes);
					return;
				}
				
				List<BlockPos> outline = new ArrayList<>();
				
				if (!nodes.get(0).equals(nodes.get(nodes.size() - 1))) nodes.add(nodes.get(0));
				
				for (int i = 0; i < nodes.size() - 1; i++) {
		            BlockPos pos1 = nodes.get(i);
		            BlockPos pos2 = nodes.get(i + 1);

		            int x1 = pos1.getX(), z1 = pos1.getZ();
		            int x2 = pos2.getX(), z2 = pos2.getZ();
		            int y = pos1.getY();
		            int tipx = x1, tipz = z1;
		            int dx = Math.abs(x2 - x1), dz = Math.abs(z2 - z1);

		            if (dx + dz == 0) {
		            	outline.add(new BlockPos(tipx, y, tipz));
		                continue;
		            }

		            int dMax = Math.max(dx, dz);
		            if (dMax == dx) {
		                for (int domstep = 0; domstep <= dx; domstep++) {
		                    tipx = x1 + domstep * (x2 - x1 > 0 ? 1 : -1);
		                    tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dx) * (z2 - z1 > 0 ? 1 : -1));

		                    outline.add(new BlockPos(tipx, y, tipz));
		                }
		            } else {
		                for (int domstep = 0; domstep <= dz; domstep++) {
		                    tipz = z1 + domstep * (z2 - z1 > 0 ? 1 : -1);
		                    tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dz) * (x2-x1>0 ? 1 : -1));

		                    outline.add(new BlockPos(tipx, y, tipz));
		                }
		            }
		        }
				
				setProgress(0.5);
				outline = outline.stream().distinct().collect(Collectors.toList());
				if (rotate) {
					setProgress(0.75);
					outline = rotate(outline);
				}
				
				setProgress(1);
		        setOutput(outline);
			}
		};
	}
	
	private List<BlockPos> rotate(List<BlockPos> outline) {
		final BlockPos[][] mutations = new BlockPos[][] {
			{new BlockPos(1, 0, 0), new BlockPos(2, 0, 0)}, {new BlockPos(-1, 0, 0), new BlockPos(-2, 0, 0)}, {new BlockPos(0, 0, 1), new BlockPos(0, 0, 2)},
			{new BlockPos(0, 0, -1), new BlockPos(0, 0, -2)}, {new BlockPos(1, 0, 0), new BlockPos(2, 0, 0), new BlockPos(2, 0, 1)},
			{new BlockPos(-1, 0, 0), new BlockPos(-2, 0, 0), new BlockPos(-2, 0, 1)}, {new BlockPos(1, 0, 0), new BlockPos(2, 0, 0), new BlockPos(2, 0, -1)},
			{new BlockPos(-1, 0, 0), new BlockPos(-2, 0, 0), new BlockPos(-2, 0, -1)}, {new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(1, 0, 2)},
			{new BlockPos(0, 0, 1), new BlockPos(0, 0, 2), new BlockPos(-1, 0, 2)}, {new BlockPos(0, 0, -1), new BlockPos(0, 0, -2), new BlockPos(1, 0, -2)},
			{new BlockPos(0, 0, -1), new BlockPos(0, 0, -2), new BlockPos(-1, 0, -2)}
		};
		
		List<BlockPos> best = new ArrayList<>(outline);
		int bestCorners = Integer.MAX_VALUE;
		
		BlockPosMatrix matrix = new BlockPosMatrix(outline);
		for (int i = 0; i < 360; i++) {
			List<BlockPos> newOutline = matrix.rotate(1);

			for (BlockPos b: new ArrayList<>(newOutline)) {
				for (BlockPos[] bl: mutations) {
					int l = bl.length - 1;

					if (!newOutline.contains(b.add(bl[l]))) continue;

					boolean shouldAdd = true;
					for (int j = 0; j < l; j++) {
						if (newOutline.contains(b.add(bl[j]))) {
							shouldAdd = false;
							break;
						}
					}

					if (shouldAdd) {
						for (int j = 0; j < l; j++) newOutline.add(b.add(bl[j]));
					}
				}
			}
			
			for (BlockPos b: new ArrayList<>(newOutline)) {
				List<BlockPos> touching = new ArrayList<>();
				
				for (BlockPos b1: new ArrayList<>(newOutline)) {
					if (b == b1) continue;
					
					if (Math.abs(b.getX() - b1.getX()) <= 1 && Math.abs(b.getZ() - b1.getZ()) <= 1) {
						touching.add(b1);
					}
				}
				
				if (touching.size() == 2) {
					if (Math.abs(touching.get(0).getX() - touching.get(1).getX()) + Math.abs(touching.get(0).getZ() - touching.get(1).getZ()) == 2
							&& touching.get(0).getX() != touching.get(1).getX() && touching.get(0).getZ() != touching.get(1).getZ()) newOutline.remove(b);
				}
			}
			
			int corners = 0;
			for (int j = 1; j < newOutline.size(); j++) {
				if (newOutline.get(j - 1).getX() != newOutline.get(j).getX() || newOutline.get(j - 1).getZ() != newOutline.get(j).getZ()) corners++;
			}
			
			if (corners < bestCorners) {
				best = newOutline;
				bestCorners = corners;
			}
		}
		
		return best;
	}
	
	protected boolean useMainThread() {
		return false;
	}
}
