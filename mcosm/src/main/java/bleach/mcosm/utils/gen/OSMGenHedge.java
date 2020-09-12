package bleach.mcosm.utils.gen;

import java.util.Random;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class OSMGenHedge extends WorldGenAbstractTree {
	
	private static final IBlockState LEAF = Blocks.LEAVES.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockOldLeaf.CHECK_DECAY, false);
	protected int height;

	public OSMGenHedge(boolean notify, int height) {
		super(notify);
		this.height = height;
	}

	public boolean generate(World worldIn, Random rand, BlockPos position) {
		setBlockAndNotifyAdequately(worldIn, position, LEAF);
		
		for (int i = 1; i <= height; i++) {
			setBlockAndNotifyAdequately(worldIn, position.up(i), LEAF);
		}
		
		for (EnumFacing f: EnumFacing.HORIZONTALS) {
			if (worldIn.getBlockState(position.offset(f)).getMaterial().isReplaceable()) {
				setBlockAndNotifyAdequately(worldIn, position.offset(f), Blocks.TRAPDOOR.getDefaultState()
						.withProperty(BlockTrapDoor.FACING, f)
						.withProperty(BlockTrapDoor.HALF, BlockTrapDoor.DoorHalf.BOTTOM)
						.withProperty(BlockTrapDoor.OPEN, true));
			}
		}
		
		return true;
	}
}
