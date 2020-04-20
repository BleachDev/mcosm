package bleach.mcosm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import bleach.mcosm.api.ApiDataHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

public class OSMCommand extends CommandBase {

	@Override
	public String getName() {
		return "osm";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/osm <path to interpreter file> [local:true/false]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0 || args.length > 2) throw new WrongUsageException(getUsage(sender), new Object[0]);
		
		boolean local = false;
		try {
			local = Boolean.parseBoolean(args[1]);
		} catch (Exception e) { }
		
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
