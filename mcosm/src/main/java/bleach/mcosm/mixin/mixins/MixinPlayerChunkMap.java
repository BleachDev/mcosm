package bleach.mcosm.mixin.mixins;

import java.util.HashSet;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;

/**
 * 
 * @author Bleach
 * 
 * Patch used to allow concurrent serverside block placing in the player chunk.
 * 
 **/

@Mixin(PlayerChunkMap.class)
public class MixinPlayerChunkMap {
	
	/*@Shadow
	private Set<PlayerChunkMapEntry> dirtyEntries;
	
	@Inject(method = "tick", at = @At("HEAD"))
	public void tick(CallbackInfo ci) {
		if (!this.dirtyEntries.isEmpty()) {
			while (true) {
				try {
					HashSet<PlayerChunkMapEntry> dirty = new HashSet<>(dirtyEntries);
					for (PlayerChunkMapEntry p: dirty) {
						p.update();
					}
					
					this.dirtyEntries.clear();
					break;
				} catch (Exception e) { }
			}
        }
	}*/
}
