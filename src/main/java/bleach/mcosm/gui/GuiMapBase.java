package bleach.mcosm.gui;

import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

public class GuiMapBase extends GuiScreen {

	private static DynamicTexture mapTexture;
	private static ResourceLocation mapResource;

	static {
		try {
			mapTexture = new DynamicTexture(ImageIO.read(GuiMapBase.class.getClassLoader().getResourceAsStream("assets/bleach/res/map.png")));
			mapResource = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("mcosm-map", mapTexture);
		} catch (IOException e) { e.printStackTrace(); }
	}

	protected double lat = 0, lon = 0, lat1 = 0, lon1 = 0;
	protected int mapX, mapY, mapLen, mapHei;

	public GuiMapBase(double lat, double lon, double lat1, double lon1) {
		this.lat = Math.max(lat, lat1);
		this.lon = Math.min(lon, lon1);
		this.lat1 = Math.min(lat, lat1);
		this.lon1 = Math.max(lon, lon1);
	}

	public void initGui() {
		mapX = width / 4;
		mapLen = width / 2;
		mapY = height / 2 - width / 8;
		mapHei = width / 4;
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();

		drawMap(mapX, mapY, mapLen, mapHei);

		// i want die
		if (lat != 0 && lon != 0 && lat1 != 0 && lon1 != 0) {
			int selX = (int) (mapX + (mapLen * ((lon + 179) / 360d)));
			int selY = (int) (mapY + (mapHei * (1 - ((lat + 91) / 180d))));
			int selLen = (int) (mapX + (mapLen * ((lon1 + 181) / 360d)));
			int selHei = (int) (mapY + (mapHei * (1 - ((lat1 + 89) / 180d))));
			for (int x = 0; x <= mapLen; x++) {
				for (int y = 0; y <= mapHei; y++) {
					if (((x + mapX == selX || x + mapX == selLen) && y + mapY >= selY && y + mapY <= selHei)
							|| ((y + mapY == selY || y + mapY == selHei) && x + mapX >= selX && x + mapX <= selLen)) {
						drawRect(mapX + x, mapY + y, mapX + x + 1, mapY + y + 1, 0xffffa000);
					}
				}
			}
		}

		long areaSize = (long) (Math.abs(lat1 - lat) * 100000) * (long) (Math.abs(lon1 - lon) * 100000);
		drawString(fontRenderer, "Selected: " + areaSize + "m\u00b2 ("
				+ Math.round(Math.abs(lat1 - lat) * 100000) + "m*" + Math.round(Math.abs(lon1 - lon) * 100000) + "m)"
				, mapX + 2, mapY + mapHei - 9, 0xc0c0c0);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	private void drawMap(int x, int y, int w, int h) {
		drawGradientRect(x - 1, y - 1, x + w + 1, y + h + 1, 0xff7070c0, 0xff70c070);
		mapTexture.updateDynamicTexture();
		Minecraft.getMinecraft().getTextureManager().bindTexture(mapResource);
		drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, w, h);
	}
}
