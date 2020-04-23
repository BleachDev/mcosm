package bleach.mcosm;

import java.util.ArrayList;
import java.util.List;

import bleach.mcosm.struct.Creatable;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;

public class OSMInstance {

	private final List<Creatable> structures = new ArrayList<>();
	private int tick = 0;
	
	public OSMInstance() {}
	
	public void add(Creatable c) {
		structures.add(c);
	}
	
	public void tick() {
		if (Minecraft.getMinecraft().world == null || Minecraft.getMinecraft().player == null) {
			stop();
    		structures.clear();
    		return;
    	}
		
    	if (!structures.isEmpty()) {
    		tick++;
    		
    		if (tick > 3600) {
    			Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentString("\u00a76Catching Up.."));
    			
    			if (tick > 3900) tick = 0;
    			
    			return;
    		}
    		
    		Creatable c = structures.get(0);
    		if (c.progress != "") Minecraft.getMinecraft().ingameGUI.addChatMessage(
    				ChatType.GAME_INFO, new TextComponentString("\u00a75" + structures.size() + " Queue | \u00a76" + c.progress));
    		
    		if (c.tick()) {
    			structures.remove(c);
    			
    			if (structures.isEmpty()) {
    				Minecraft.getMinecraft().ingameGUI.addChatMessage(ChatType.GAME_INFO, new TextComponentString("\u00a76Done!"));
    			} else {
    				System.out.println("Done! Queue: " + structures.size());
    			}
    		}
    	}
	}
	
	public void stop() {
		for (Creatable c: structures) c.stop();
	}
}
