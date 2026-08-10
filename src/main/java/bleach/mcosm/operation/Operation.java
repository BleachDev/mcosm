package bleach.mcosm.operation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.apache.commons.lang3.StringUtils;

public abstract class Operation {

	protected OperationThread<?> thread = new OperationThread<>();
	
	protected boolean started = false;

	private static int longestNameWidth = 0;
	private static final int maxProgressWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth("100.0");
	
	public void start() {
		if (!started) {
			if (useMainThread()) thread.run();
			else thread.start();
			started = true;
		}
	}
	
	/** Returns true if the operation is done **/
	public boolean tick() {
		if (thread.isSleeping()) {
			return false;
		} if (started) {
			return !thread.isAlive();
		} else {
			start();
			return false;
		}
	}
	
	public Object getResult() {
		return thread.getOutput();
	}
	
	public double getProgress() {
		return thread.getProgress();
	}
	
	public boolean isSleeping() {
		return thread.isSleeping();
	}
	
	public String getSleepReason() {
		return thread.getSleepReason();
	}
	
	@SuppressWarnings("deprecation")
	public void kill() {
		thread.stop(); // yikes
	}
	
	protected abstract boolean useMainThread();

	public String getFormattedName() {
		String operationName = getClass().getSimpleName().replace("Operation", "");
		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int operationNameWidth = fontRenderer.getStringWidth(operationName);

		if (longestNameWidth < operationNameWidth)
			longestNameWidth = operationNameWidth;

		int spaceWidth = fontRenderer.getCharWidth(' ');

		// Add additional spaces according to the longest so far seen name, reducing text on the screen jumping around so much
		return operationName + StringUtils.repeat(' ', Math.max(0, (longestNameWidth - operationNameWidth) / spaceWidth));
	}

	public String getProgressFormatted() {
		String progress = String.valueOf(Math.round(getProgress() * 10000) / 100d);

		FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		int progressWidth = fontRenderer.getStringWidth(progress);
		int spaceWidth = fontRenderer.getCharWidth(' ');

		return StringUtils.repeat(' ', Math.max(0, (maxProgressWidth - progressWidth) / spaceWidth)) + progress;
	}
}
