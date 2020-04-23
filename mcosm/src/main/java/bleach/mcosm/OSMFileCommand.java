package bleach.mcosm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import bleach.mcosm.api.ApiDataHandler;
import bleach.mcosm.gui.GuiMapBase;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class OSMFileCommand extends CommandBase {

	@Override
	public String getName() {
		return "osmfile";
	}
 
	@Override
	public String getUsage(ICommandSender sender) {
		return "/osmfile <path to interpreter file> [local/global]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		//McOSM.guiQueue.add(new GuiMapBase(1, 1, 2, 2));
		if (args.length == 0 || args.length > 2) throw new WrongUsageException(getUsage(sender), new Object[0]);
		
		boolean local = false;
		if (args.length == 2) {
			if (args[1].equalsIgnoreCase("local")) {
				local = true;
			} else if (!args[1].equalsIgnoreCase("global")) {
				throw new CommandException("Invalid Arg 1: \"" + args[1] + "\", should be \"local\" or \"global\"!", new Object[0]);
			}
		}
		
		try {
			String data = new String(Files.readAllBytes(Paths.get(args[0])));
			ApiDataHandler apiHandler = new ApiDataHandler(data, local ? ApiDataHandler.Projection.BTE_PLAYER : ApiDataHandler.Projection.BTE_00);
			apiHandler.addToInstance(McOSM.osmInst);
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new CommandException("Invalid Path, Path Should Be: " + "C:\\example\\folder\\interpreter.json", new Object[0]);
		}
	}

}
