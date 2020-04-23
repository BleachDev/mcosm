package bleach.mcosm.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.minecraft.client.gui.GuiScreen;

public class GuiMapBase extends GuiScreen {

	private static BufferedImage mapImage;
	
	static {
		try {
			mapImage = ImageIO.read(GuiMapBase.class.getClassLoader().getResourceAsStream("assets/bleach/res/map.png"));
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	protected double lat, lon, lat1, lon1;
	protected int mapX, mapY, mapLen, mapHei;
	
	public GuiMapBase(int lat, int lon, int lat1, int lon1) {
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
        
        Image tmp = mapImage.getScaledInstance(mapLen + 1, mapHei + 1, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(mapLen + 1, mapHei + 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        
        int selX = (int) (mapX + (mapLen * ((lon + 90) / 180d)));
        int selY = (int) (mapY + (mapHei * ((lat1 + 90) / 180d)));
        int selLen = (int) (mapX + (mapLen * ((lon1 + 90) / 180d)));
        int selHei = (int) (mapY + (mapHei * ((lat + 90) / 180d)));
        for (int x = 0; x <= mapLen; x++) {
        	for (int y = 0; y <= mapHei; y++) {
        		if (x + mapX >= selX && x + mapX <= selLen && y + mapY >= selY && y + mapY <= selHei) {
        			drawRect(mapX + x, mapY + y, mapX + x + 1, mapY + y + 1, 0xffffa000);
        		} else {
        			drawRect(mapX + x, mapY + y, mapX + x + 1, mapY + y + 1, resized.getRGB(x, y));
        		}
            }
        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
