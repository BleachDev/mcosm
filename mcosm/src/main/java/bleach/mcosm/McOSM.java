package bleach.mcosm;

import java.util.LinkedList;
import java.util.Queue;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(modid = McOSM.MODID, name = McOSM.NAME, version = McOSM.VERSION)
public class McOSM {
    public static final String MODID = "mcosm";
    public static final String NAME = "McOSM";
    public static final String VERSION = "0.3.0";
    
    public static OSMInstance osmInst = new OSMInstance();
    
    public static Queue<GuiScreen> guiQueue = new LinkedList<>();
    
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
    	osmInst.tick();
    }
}
