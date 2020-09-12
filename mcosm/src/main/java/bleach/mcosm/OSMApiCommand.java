package bleach.mcosm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class OSMApiCommand extends CommandBase {

	@Override
	public String getName() {
		return "osmapi";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/osmapi <lat1> <lon1> <lat2> <lon2>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	    
		if (args.length < 4) {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		}
		
		double minLat, minLon, maxLat, maxLon;
		
		try { minLat = Double.parseDouble(args[0]); } catch (Exception e) { throw new CommandException("Invalid Coordinate [Arg 1]", new Object[0]); }
		try { minLon = Double.parseDouble(args[1]); } catch (Exception e) { throw new CommandException("Invalid Coordinate [Arg 2]", new Object[0]); }
		try { maxLat = Double.parseDouble(args[2]); } catch (Exception e) { throw new CommandException("Invalid Coordinate [Arg 3]", new Object[0]); }
		try { maxLon = Double.parseDouble(args[3]); } catch (Exception e) { throw new CommandException("Invalid Coordinate [Arg 4]", new Object[0]); }

		String link = getApiLink(minLat, minLon, maxLat, maxLon);
		
		GuiScreen.setClipboardString(link);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(new TextComponentString("Added api link to clipboard"));
	}
	
	public static String getApiLink(double minLat, double minLon, double maxLat, double maxLon) {
		if (minLat > maxLat) {
			double tempLat = minLat;
			minLat = maxLat;
			maxLat = tempLat;
		}
		
		if (minLon > maxLon) {
			double tempLon = minLon;
			minLon = maxLon;
			maxLon = tempLon;
		}
		
		String coords = minLat + "," + minLon + "," + maxLat + "," + maxLon;
		String boundCoords = (minLat - 0.0002) + "," + (minLon - 0.0002) + "," + (maxLat + 0.0002) + "," + (maxLon + 0.0002);
		return "https://overpass-api.de/api/interpreter?data=[out:json];(way(" + coords + ");node[natural=tree](" + coords + "););out geom(" + boundCoords + ");";
	}
	

}
