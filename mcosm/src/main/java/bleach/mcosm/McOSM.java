package bleach.mcosm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(modid = McOSM.MODID, name = McOSM.NAME, version = McOSM.VERSION)
public class McOSM
{
    public static final String MODID = "mcosm";
    public static final String NAME = "McOSM";
    public static final String VERSION = "0.2.5";
    
    public static OSMInstance osmInst = new OSMInstance();
    
    public static Queue<GuiScreen> guiQueue = new LinkedList<>();
    
    private long tickCount = 0;
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(this);
    	
    	ClientCommandHandler.instance.registerCommand(new OSMFileCommand());
    	ClientCommandHandler.instance.registerCommand(new OSMApiCommand());
    }
    
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
    	if (event.phase == Phase.END && !guiQueue.isEmpty()) {
    		Minecraft.getMinecraft().displayGuiScreen(guiQueue.poll());
    	}
    }
    
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
    	if (event.phase == Phase.END) return;
    	
    	// Chunk Garbage Collector For CubicChunks
    	if (tickCount % 200 == 0) {
	    	try {
	    		int mcX = (int) Minecraft.getMinecraft().player.posX;
	    		int mcZ = (int) Minecraft.getMinecraft().player.posZ;
	    		
		    	World sw = Minecraft.getMinecraft().getIntegratedServer().getWorld(Minecraft.getMinecraft().player.dimension);
		    	ChunkProviderServer cps = (ChunkProviderServer) sw.getChunkProvider();
		    	
		    	//int prevSize = cps.loadedChunks.size();
		    	//int i = 0;
		    	for (Entry<Long, Chunk> c: new HashMap<>(cps.loadedChunks).entrySet()) {
		    		long lon = c.getKey();
		    		int x = (int) (lon & 4294967295L);
		    		int z = (int) (lon >> 32);
		    		 
		    		// Manually unload chunk because CubicChunks disables queueUnload??
		    		if(Math.abs(x * 16 - mcX) > 300 || Math.abs(z * 16 - mcZ) > 300) {
		    			if (sw.getPersistentChunks().keySet().contains(new ChunkPos(x, z))) continue;
		    			
		    			Chunk ch = c.getValue();
		    			if (ch == null) continue;
		    			
		    			ch.unloadQueued = true;
		    			ch.onUnload();
                        ForgeChunkManager.putDormantChunk(ChunkPos.asLong(x, z), ch);
                        ch.setLastSaveTime(sw.getTotalWorldTime());
                        cps.chunkLoader.saveChunk(sw, ch);
                        cps.chunkLoader.saveExtraChunkData(sw, ch);
                        cps.loadedChunks.remove(lon);
		    		}
		    		
		    		// debug
		    		//if (i < 10) Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString(c.toString()));
		    		//i++;
		    	}
		    	
		    	//Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
		    	//		new TextComponentString("\u00a76" + prevSize + " => " + cps.loadedChunks.size()));
	    	} catch (Exception e) {}
    	}
    	
    	osmInst.tick();
    	
    	tickCount++;
    }
}
