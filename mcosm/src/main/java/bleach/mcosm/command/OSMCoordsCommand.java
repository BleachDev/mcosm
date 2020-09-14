package bleach.mcosm.command;

import org.apache.commons.lang3.math.NumberUtils;

import bleach.mcosm.utils.GeoPos;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class OSMCoordsCommand extends CommandBase {

	@Override
	public String getName() {
		return "osmcoords";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/osmcoords from <X> <Z> | /osmcoords to <lat> <lon>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length != 3 || !NumberUtils.isCreatable(args[1]) || !NumberUtils.isCreatable(args[2])) {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		}
		
		if (args[0].equalsIgnoreCase("from")) {
			double[] d = GeoPos.toLatLonBTE(new BlockPos(NumberUtils.createNumber(args[1]).doubleValue(), 0, NumberUtils.createNumber(args[2]).doubleValue()));
			ITextComponent text = new TextComponentString("Position to Geo Pos: \u00a7a" + d[0] + ", " + d[1]);
			
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
					text.setStyle(text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, d[0] + " " + d[1]))
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Show Coords")))));
		} else if (args[0].equalsIgnoreCase("to")) {
			BlockPos bp = GeoPos.from00BTE(NumberUtils.createNumber(args[1]).doubleValue(), NumberUtils.createNumber(args[2]).doubleValue(), 0);
			ITextComponent text = new TextComponentString("Geo Pos to Position: \u00a7a" + bp.getX() + ", " + bp.getZ());
			
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(
					text.setStyle(text.getStyle().setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, bp.getX() + " " + bp.getZ()))
							.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Show Coords")))));
		} else {
			throw new WrongUsageException(getUsage(sender), new Object[0]);
		}
	}
}
