package bleach.mcosm.operation.nodes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import bleach.mcosm.operation.Operation;
import bleach.mcosm.operation.OperationThread;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;

public class MakeStripeLineOperation extends Operation {

	public MakeStripeLineOperation(List<BlockPos> nodes, IBlockState state, IBlockState stripe, int stripeLen, int thickness) {
		
		thread = new OperationThread<LinkedHashMap<BlockPos, IBlockState>>() {
			
			public void run() {
				if (nodes.size() == 0) {
					setOutput(new LinkedHashMap<>());
					return;
				}
				
				List<BlockPos> line = new ArrayList<>();
				
				for (int i = 0; i < nodes.size() - 1; i++) {
		            BlockPos pos1 = nodes.get(i);
		            BlockPos pos2 = nodes.get(i + 1);

		            int x1 = pos1.getX(), z1 = pos1.getZ();
		            int x2 = pos2.getX(), z2 = pos2.getZ();
		            int y = pos1.getY();
		            int tipx = x1, tipz = z1;
		            int dx = Math.abs(x2 - x1), dz = Math.abs(z2 - z1);

		            if (dx + dz == 0) {
		            	line.add(new BlockPos(tipx, y, tipz));
		                continue;
		            }

		            int dMax = Math.max(dx, dz);
		            if (dMax == dx) {
		                for (int domstep = 0; domstep <= dx; domstep++) {
		                    tipx = x1 + domstep * (x2 - x1 > 0 ? 1 : -1);
		                    tipz = (int) Math.round(z1 + domstep * ((double) dz) / ((double) dx) * (z2 - z1 > 0 ? 1 : -1));

		                    line.add(new BlockPos(tipx, y, tipz));
		                }
		            } else {
		                for (int domstep = 0; domstep <= dz; domstep++) {
		                    tipz = z1 + domstep * (z2 - z1 > 0 ? 1 : -1);
		                    tipx = (int) Math.round(x1 + domstep * ((double) dx) / ((double) dz) * (x2-x1>0 ? 1 : -1));

		                    line.add(new BlockPos(tipx, y, tipz));
		                }
		            }
		        }
				
				setProgress(0.5);
				line = line.stream().distinct().collect(Collectors.toList());
				
				LinkedHashMap<BlockPos, IBlockState> filledLine = new LinkedHashMap<>();
				int cur = -stripeLen;
				for (BlockPos b: new ArrayList<>(line)) {
					if (cur > 0) filledLine.put(b, stripe);
					else filledLine.put(b, state);
					
					for (int x = -thickness; x <= thickness; x++) {
						for (int z = -thickness; z <= thickness; z++) {
							if (Math.sqrt(Math.abs(x) * Math.abs(z)) <= thickness && !line.contains(b.add(x, 0, z)) && !filledLine.containsKey(b.add(x, 0, z))) {
								filledLine.put(b.add(x, 0, z), state);
							}
						}
					}
					
					cur++;
					if (cur > stripeLen) cur = -stripeLen;
				}
				
				setProgress(1);
				setOutput(filledLine);
			}
		};
				
	}
	
	protected boolean useMainThread() {
		return false;
	}
}
