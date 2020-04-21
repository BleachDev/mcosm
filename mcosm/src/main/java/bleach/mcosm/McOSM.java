package bleach.mcosm;

import net.minecraftforge.client.ClientCommandHandler;
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
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(this);
    	
    	ClientCommandHandler.instance.registerCommand(new OSMCommand());
    	ClientCommandHandler.instance.registerCommand(new OSMApiCommand());
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.WorldTickEvent event) {
    	if (event.phase == Phase.END) return;
    	
    	osmInst.tick();
    }
}
