package bleach.mcosm.utils.gen;

import java.util.Random;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class OSMGenCanopyTree extends WorldGenAbstractTree {
	
	private static final IBlockState LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockOldLeaf.CHECK_DECAY, false);

	public OSMGenCanopyTree(boolean notify) {
		super(notify);
	}

	public boolean generate(World worldIn, Random rand, BlockPos position) {
		int height = 4 + rand.nextInt(2);
		
		for (int i = 0; i < height; i++) {
			setBlockAndNotifyAdequately(worldIn, position.up(i), Blocks.LOG.getDefaultState());
		}
		
		for (EnumFacing f: EnumFacing.HORIZONTALS) {
			for (int i = 1; i <= height; i++) {
				setBlockAndNotifyAdequately(worldIn, position.offset(f).up(i), LEAF);
				
				if (i == 2 || i == 3) {
					setBlockAndNotifyAdequately(worldIn, position.offset(f).offset(f.rotateY()).up(i), LEAF);
				}
			}
		}
		
		setBlockAndNotifyAdequately(worldIn, position.up(height), LEAF);
		setBlockAndNotifyAdequately(worldIn, position.up(height + 1), LEAF);
		
		return true;
	}
}
