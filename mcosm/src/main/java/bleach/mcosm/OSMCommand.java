package bleach.mcosm;

import bleach.mcosm.gui.GuiOSM;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class OSMCommand extends CommandBase {

	@Override
	public String getName() {
		return "osm";
	}
 
	@Override
	public String getUsage(ICommandSender sender) {
		return "/osm <lat1> <lon1> <lat2> <lon2> | /osm stop";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 4) {
			try {
				McOSM.guiQueue.add(new GuiOSM(Double.parseDouble(args[0]), Double.parseDouble(args[1]),
						Double.parseDouble(args[2]), Double.parseDouble(args[3])));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				throw new CommandException("Invalid Coordinate [Arg ?]", new Object[0]);
			}
			
		} else if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
			McOSM.osmInst.stop();
			sender.sendMessage(new TextComponentString("Stopped all running OSM instances!"));
		} else {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		}
	}

}
