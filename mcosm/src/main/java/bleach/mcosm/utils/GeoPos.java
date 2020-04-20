package bleach.mcosm.utils;

import java.text.DecimalFormat;

import bleach.mcosm.utils.projection.ModifiedAiroceanProj;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

public class GeoPos {
	
	private static ModifiedAiroceanProj bteProj = new ModifiedAiroceanProj();

	public static BlockPos from00Naive(double lat, double lon, int y) {
		return new BlockPos(latLonToBlockNaive(lon, 0), y, latLonToBlockNaive(lat, 0));
	}
	
	public static BlockPos fromPlayerNaive(double lat, double lon, double lat0, double lon0) {
		return new BlockPos(
				Minecraft.getMinecraft().player.posX + latLonToBlockNaive(lon, lon0), // Lon / X+ = East
				Minecraft.getMinecraft().player.posY,
				Minecraft.getMinecraft().player.posZ - latLonToBlockNaive(lat, lat0)); // Lat / Z- = North
	}
	
	public static BlockPos from00BTE(double lat, double lon, int y) {
		double[] latLon = bteProj.fromGeo(lon, lat);
		return new BlockPos(latLon[0], y, -latLon[1]);
	}
	
	public static BlockPos fromPlayerBTE(double lat, double lon, double lat0, double lon0) {
		double[] latLon = bteProj.fromGeo(lon, lat); // LON, LAT!!!!
		double[] latLon0 = bteProj.fromGeo(lon0, lat0);
		return new BlockPos(
				Minecraft.getMinecraft().player.posX + Math.round(latLon[0] - latLon0[0]), // Lon / X+ = East
				Minecraft.getMinecraft().player.posY,
				Minecraft.getMinecraft().player.posZ - Math.round(latLon[1] - latLon0[1])); // Lat / Z- = North
	}
	
	private static int latLonToBlockNaive(double c, double c0) {
		DecimalFormat df = new DecimalFormat("0.00000");
		double d = Double.valueOf(df.format(c)) * 100000;
		double d0 = Double.valueOf(df.format(c0)) * 100000;
		return (int) Math.round(d - d0);
	}
}
