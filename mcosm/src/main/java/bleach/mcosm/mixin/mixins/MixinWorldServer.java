package bleach.mcosm.mixin.mixins;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ReportedException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

/**
 * 
 * @author Bleach
 * 
 * Patch used to allow concurrent serverside block placing.
 * 
 **/

@Mixin(WorldServer.class)
public abstract class MixinWorldServer extends World {

	protected MixinWorldServer(ISaveHandler saveHandlerIn, WorldInfo info, WorldProvider providerIn,
			Profiler profilerIn, boolean client) {
		super(saveHandlerIn, info, providerIn, profilerIn, client);
	}
	
	/*@Shadow
	private Set<NextTickListEntry> pendingTickListEntriesHashSet;
	
	@Shadow
	private TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet;
	
	@Shadow
	private List<NextTickListEntry> pendingTickListEntriesThisTick;
	
	@Inject(method = "tickUpdates", at = @At("HEAD"), cancellable = true)
	public void tickUpdates(boolean runAllPending, CallbackInfoReturnable<Boolean> ci) {
		if (this.worldInfo.getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) ci.setReturnValue(false);
        else {
        	this.profiler.startSection("cleaning");
        	
        	int i;
    		int j;
    		Set<NextTickListEntry> newTreeSet;
    		Set<NextTickListEntry> newSet;
    		
    		while (true) {
    			try {
    				newTreeSet = new HashSet<>(pendingTickListEntriesTreeSet);
    				newSet = new HashSet<>(pendingTickListEntriesHashSet);
    				
    				i = newSet.size();
    	    		j = newTreeSet.size();
    	    		
    				if (i != j) {
    					if (i < j) {
    						newTreeSet.removeAll(pendingTickListEntriesHashSet);
    						
    						pendingTickListEntriesHashSet.addAll(newTreeSet);
    					} else {
    						newSet.removeAll(pendingTickListEntriesTreeSet);
    						
    						pendingTickListEntriesTreeSet.addAll(newSet);
    					}
    				}
    				
    				break;
    			} catch (Exception e) {}
    		}
    		
            if (i > 65536) i = 65536;

            for (int k = 0; k < i; ++k) {
                NextTickListEntry nextticklistentry = this.pendingTickListEntriesTreeSet.first();

                if (!runAllPending && nextticklistentry.scheduledTime > this.worldInfo.getWorldTotalTime())
                {
                    break;
                }
                
                while (true) { try { if(this.pendingTickListEntriesTreeSet.remove(nextticklistentry)) break; } catch(Exception e) {} }
                while (true) { try { if(this.pendingTickListEntriesHashSet.remove(nextticklistentry)) break; } catch(Exception e) {} }
                while (true) { try { if(this.pendingTickListEntriesThisTick.add(nextticklistentry)) break; } catch(Exception e) {} }
            }

            this.profiler.endSection();
            this.profiler.startSection("ticking");
            Iterator<NextTickListEntry> iterator = this.pendingTickListEntriesThisTick.iterator();

            while (iterator.hasNext()) {
                NextTickListEntry nextticklistentry1 = iterator.next();
                iterator.remove();

                if (this.isAreaLoaded(nextticklistentry1.position.add(0, 0, 0), nextticklistentry1.position.add(0, 0, 0)))
                {
                    IBlockState iblockstate = this.getBlockState(nextticklistentry1.position);

                    if (iblockstate.getMaterial() != Material.AIR && Block.isEqualTo(iblockstate.getBlock(), nextticklistentry1.getBlock()))
                    {
                        try
                        {
                            iblockstate.getBlock().updateTick(this, nextticklistentry1.position, iblockstate, this.rand);
                        }
                        catch (Throwable throwable)
                        {
                            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while ticking a block");
                            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being ticked");
                            CrashReportCategory.addBlockInfo(crashreportcategory, nextticklistentry1.position, iblockstate);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
                else
                {
                    this.scheduleUpdate(nextticklistentry1.position, nextticklistentry1.getBlock(), 0);
                }
            }

            this.profiler.endSection();
            this.pendingTickListEntriesThisTick.clear();
            ci.setReturnValue(this.pendingTickListEntriesTreeSet.isEmpty());
        }
	}*/
	
	/*@Inject(method = "getPendingBlockUpdates(Lnet/minecraft/world/gen/structure/StructureBoundingBox;Z)Ljava/util/List;", at = @At("HEAD"), cancellable = true)
	public void getPendingBlockUpdates(StructureBoundingBox structureBB, boolean remove, CallbackInfoReturnable<List<NextTickListEntry>> ci) {
		List<NextTickListEntry> list = null;
        Set<NextTickListEntry> newTreeSet;

        for (int i = 0; i < 2; ++i) {
            Iterator<NextTickListEntry> iterator;

            if (i == 0) {
            	while (true) {
                	try { 
        	        	newTreeSet = new HashSet<>(pendingTickListEntriesTreeSet);
        	        	break;
                	} catch (Exception e) {}
                }
            	
                iterator = newTreeSet.iterator();
            } else {
                iterator = this.pendingTickListEntriesThisTick.iterator();
            }

            while (iterator.hasNext()) {
                NextTickListEntry nextticklistentry = iterator.next();
                BlockPos blockpos = nextticklistentry.position;

                if (blockpos.getX() >= structureBB.minX && blockpos.getX() < structureBB.maxX && blockpos.getZ() >= structureBB.minZ && blockpos.getZ() < structureBB.maxZ) {
                    if (remove) {
                        if (i == 0) {
                        	while (true) {
                        		try {
                        			this.pendingTickListEntriesHashSet.add(nextticklistentry);
                        			break;
                        		} catch(Exception e) {}
                        	}
                        }

                        iterator.remove();

                        if (list == null) list = new ArrayList<>();
                        list.add(nextticklistentry);
                    }
                }
            }
        }
        
        ci.setReturnValue(list);
	}*/
}
