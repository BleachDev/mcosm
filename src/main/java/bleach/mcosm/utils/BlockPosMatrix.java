package bleach.mcosm.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import net.minecraft.util.math.BlockPos;

public class BlockPosMatrix {

	private List<Triple<Double /*X*/, Double /*Z*/, Integer /*Y*/>> matrix = new ArrayList<>();
	private int[] center;
	
	public BlockPosMatrix(List<BlockPos> blocks) {
		int miX = Integer.MAX_VALUE, miZ = Integer.MAX_VALUE, maX = Integer.MIN_VALUE, maZ = Integer.MIN_VALUE;
		
		
		for (BlockPos b: blocks) {
			matrix.add(Triple.of((double) b.getX(), (double) b.getZ(), b.getY()));
			if (b.getX() < miX) { miX = b.getX(); }
			if (b.getZ() < miZ) { miZ = b.getZ(); }
			if (b.getX() > maX) { maX = b.getX(); }
			if (b.getZ() > maZ) { maZ = b.getZ(); }
		}
		
		center = new int[] {(int) (maX - (maX - miX) / 2), (int) (maZ - (maZ - miZ) / 2)};
	}
	
	public List<BlockPos> rotate(int degrees) {
		double sin = Math.sin(Math.toRadians(degrees));
		double cos = Math.cos(Math.toRadians(degrees));
		
		// The New Original Matrix To Replace
		List<Triple<Double, Double, Integer>> newMatrix = new ArrayList<>();
		
		// A local matrix to to the rotate on
		List<Triple<Double, Double, Integer>> lmatrix = new ArrayList<>(matrix);
		for (int i = 0; i < lmatrix.size(); i++) {
			
			// Create local matrix aligned to 0,0
			Triple<Double, Double, Integer> newM = lmatrix.get(i);
			newM = Triple.of(newM.getLeft() - center[0], newM.getMiddle() - center[1], newM.getRight());
			
			// Rotate it
			Triple<Double, Double, Integer> rotM = Triple.of(
					(cos * newM.getLeft()) + (-sin * newM.getMiddle()), (sin * newM.getLeft()) + (cos * newM.getMiddle()), newM.getRight());
			
			newMatrix.add(Triple.of(rotM.getLeft() + center[0], rotM.getMiddle() + center[1], rotM.getRight()));
		}
		
		matrix.clear();
		matrix.addAll(newMatrix);
		
		return getBlockPoses();
	}
	
	public List<BlockPos> getBlockPoses() {
		List<BlockPos> out = new ArrayList<>();
		
		for (Triple<Double, Double, Integer> t: matrix) {
			out.add(new BlockPos(t.getLeft(), (double) t.getRight(), t.getMiddle()));
		}
		
		return out;
	}
}
