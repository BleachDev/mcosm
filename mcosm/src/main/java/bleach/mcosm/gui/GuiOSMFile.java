package bleach.mcosm.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import bleach.mcosm.McOSM;
import bleach.mcosm.api.ApiDataHandler;
import bleach.mcosm.api.ApiDataHandler.Projection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiOSMFile extends GuiMapBase {
	
	protected ApiDataHandler apiData;
	protected List<JsonObject> ways;
	protected List<JsonObject> nodes;
	
	public GuiOSMFile(ApiDataHandler apiData) {
		super(apiData.minLat, apiData.maxLon, apiData.maxLat, apiData.maxLon);
		System.out.println(apiData.minLat + " | " + apiData.maxLon + " | " + apiData.maxLat + " | " + apiData.maxLon);
		this.apiData = apiData;
		this.ways = new ArrayList<>(apiData.ways);
		this.nodes = new ArrayList<>(apiData.nodes);
	}
	
	public void initGui() {
		super.initGui();
		
		addButton(new GuiButton(0, mapX + mapLen - 75, mapY + mapHei + 5, 75, 20, "Start"));
		addButton(new GuiButton(1, mapX, mapY + mapHei + 5, 75, 20, "Cancel"));
		
		addButton(new GuiButton(2, mapX - 65, mapY, 60, 20, "\u00a7aBuildings"));
		addButton(new GuiButton(3, mapX - 65, mapY + 22, 60, 20, "\u00a7aRoads"));
		addButton(new GuiButton(4, mapX - 65, mapY + 44, 60, 20, "\u00a7aTrees"));
		
		addButton(new GuiButton(5, mapX - 65, mapY + 72, 60, 20, "Global"));
    }
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		drawString(fontRenderer, "Structures: " + (ways.size() + nodes.size()), mapX + 2, mapY - 10, 0x90a0a0);
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
			case 0:
				updateLists();
				apiData.ways = new ArrayList<>(ways);
				apiData.nodes = new ArrayList<>(nodes);
				if (buttonList.get(5).displayString.equals("Local")) apiData.proj = Projection.BTE_PLAYER;
				apiData.addToInstance(McOSM.osmInst);
				Minecraft.getMinecraft().displayGuiScreen(null);
				break;
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(null);
				break;
			case 2: case 3: case 4:
				String s = button.displayString;
				button.displayString = (s.startsWith("\u00a7c") ? "\u00a7a" : "\u00a7c") + s.substring(2);
				updateLists();
				break;
			case 5:
				button.displayString = button.displayString.equals("Global") ? "Local" : "Global";
		}
    }
	
	protected void updateLists() {
		if (apiData != null) {
			ways = apiData.ways.stream().filter(j -> 
					(buttonList.get(2).displayString.startsWith("\u00a7a") && j.get("tags").getAsJsonObject().has("building"))
					|| (buttonList.get(3).displayString.startsWith("\u00a7a") && j.get("tags").getAsJsonObject().has("highway"))
					|| (buttonList.get(4).displayString.startsWith("\u00a7a")
							&& (j.get("tags").getAsJsonObject().has("natural") || j.get("tags").getAsJsonObject().has("barrier"))))
					.collect(Collectors.toList());

			nodes = apiData.nodes.stream().filter(j -> buttonList.get(4).displayString.startsWith("\u00a7a")).collect(Collectors.toList());
		}
	}
}
