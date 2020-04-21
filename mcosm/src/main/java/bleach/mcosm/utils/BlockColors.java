package bleach.mcosm.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class BlockColors {

	private static HashMap<ResourceLocation, Integer> COLORS = null;
	
	private static void initBlockColors() {
		COLORS = new HashMap<>();
		
		for (Block block : Block.REGISTRY) {
			IBlockState state = block.getBlockState().getBaseState();
			
			if (!state.isFullBlock() || state.isTranslucent()) continue;
			
			try {
				TextureAtlasSprite atlas = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
				
				final int iconWidth = atlas.getIconWidth();
				final int iconHeight = atlas.getIconHeight();
				final int frameCount = atlas.getFrameCount();
				if (iconWidth <= 0 || iconHeight <= 0 || frameCount <= 0) continue;
				
				BufferedImage img = new BufferedImage(iconWidth, iconHeight * frameCount, BufferedImage.TYPE_4BYTE_ABGR);
				
				int[][] frameTextureData = atlas.getFrameTextureData(0);
				int[] largestMipMapTextureData = frameTextureData[0];
				img.setRGB(0, 0, iconWidth, iconHeight, largestMipMapTextureData, 0, iconWidth);
				
				int avg = averageImage(img);
				COLORS.put(block.getRegistryName(), avg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static int averageImage(BufferedImage img) {
		long sumr = 0, sumg = 0, sumb = 0;
	    for (int x = 0; x < img.getWidth(); x++) {
	        for (int y = 0; y < img.getHeight(); y++) {
	            Color pixel = new Color(img.getRGB(x, y));
	            sumr += pixel.getRed();
	            sumg += pixel.getGreen();
	            sumb += pixel.getBlue();
	        }
	    }
	    
	    int pixels = img.getWidth() * img.getHeight();
	    return (int) (((int) ((sumr / pixels) << 16) | (sumg / pixels) << 8) | (sumb / pixels));
	}
	
	public static Block getClosestBlock(int color) {
		if (COLORS == null) initBlockColors();
		
		if (Integer.toHexString(color).length() > 6) {
			color = color & 0xFFFFFF;
		}
		
		ResourceLocation closestLoc = Blocks.STONE.getRegistryName();
		int diff = Integer.MAX_VALUE;
		for (Entry<ResourceLocation, Integer> e: COLORS.entrySet()) {
			int rgb = e.getValue();
			int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = (rgb) & 0xFF;
			int r1 = (color >> 16) & 0xFF, g1 = (color >> 8) & 0xFF, b1 = (color) & 0xFF;
			int newDiff = Math.abs(r - r1) + Math.abs(g - g1) + Math.abs(b - b1);
			
			if (newDiff < diff) {
				diff = newDiff;
				closestLoc = e.getKey();
			}
		}
		
		System.out.println(closestLoc + " | " + diff);
		return Block.REGISTRY.getObject(closestLoc);
	}
}
