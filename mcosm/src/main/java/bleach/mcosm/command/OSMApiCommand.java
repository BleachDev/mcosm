package bleach.mcosm.command;

import org.apache.commons.lang3.math.NumberUtils;

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
		
		if (NumberUtils.isCreatable(args[0])) minLat = NumberUtils.createNumber(args[0]).doubleValue();
		else throw new CommandException("Invalid Coordinate [Arg 1]", new Object[0]);
		
		if (NumberUtils.isCreatable(args[1])) minLon = NumberUtils.createNumber(args[1]).doubleValue();
		else throw new CommandException("Invalid Coordinate [Arg 2]", new Object[0]);
		
		if (NumberUtils.isCreatable(args[2])) maxLat = NumberUtils.createNumber(args[2]).doubleValue();
		else throw new CommandException("Invalid Coordinate [Arg 3]", new Object[0]);
		
		if (NumberUtils.isCreatable(args[3])) maxLon = NumberUtils.createNumber(args[3]).doubleValue();
		else throw new CommandException("Invalid Coordinate [Arg 4]", new Object[0]);

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
