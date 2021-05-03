package bleach.mcosm.command;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bleach.mcosm.McOSM;
import bleach.mcosm.api.ApiDataHandler;
import bleach.mcosm.api.ApiDataHandler.Projection;
import bleach.mcosm.utils.GeoPos;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class OSMShellCommand extends CommandBase {

	@Override
	public String getName() {
		return "osmshell";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/osmshell";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		double[] latLon = GeoPos.toLatLonBTE(sender.getPosition());
		
		try {
			String coords = (latLon[0] - 0.001) + "," + (latLon[1] - 0.001) + "," + (latLon[0] + 0.001) + "," + (latLon[1] + 0.001);
			String link = "https://overpass-api.de/api/interpreter?data="
					+ URLEncoder.encode("[out:json];(way[building](" + coords + "););out geom;", "utf-8");
			
			URL url = new URL(link);
			System.out.println(url);
			
			URLConnection con = url.openConnection();
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/75.0.1");
			con.setConnectTimeout(15000);
			con.setReadTimeout(15000);
			
			String response = IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
			sender.sendMessage(new TextComponentString("\u00a7aDownloaded (" + response.length() + " Bytes)"));
			
			new JsonParser().parse(response); // Validate that it's real json
			
			ApiDataHandler apiData = new ApiDataHandler(response, Projection.BTE_00);
			
			List<JsonObject> newWays = apiData.ways.stream().filter(j -> {
				try {
					JsonObject jbounds = j.get("bounds").getAsJsonObject();
					//System.out.println(jbounds);
					return jbounds.get("minlat").getAsDouble() < latLon[0] && jbounds.get("minlon").getAsDouble() < latLon[1]
							&& jbounds.get("maxlat").getAsDouble() > latLon[0] && jbounds.get("maxlon").getAsDouble() > latLon[1];
				} catch (Exception e) {
					sender.sendMessage(new TextComponentString("\u00a7cError Getting Building Pos"));
					return false;
				}
			}).collect(Collectors.toList());
			
			apiData.ways = newWays;
			
			sender.sendMessage(new TextComponentString("\u00a7aLoaded " + apiData.ways.size() + " Building(s)"));
			apiData.addToInstance(McOSM.osmInst);
			
		} catch (Exception e) {
			sender.sendMessage(new TextComponentString("\u00a7cError: " + e.getClass().getSimpleName()));
			sender.sendMessage(new TextComponentString(Arrays.toString(e.getStackTrace())));
			e.printStackTrace();
		}
	}

}
