package bleach.mcosm.gui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import bleach.mcosm.McOSM;
import bleach.mcosm.OSMApiCommand;
import bleach.mcosm.api.ApiDataHandler;
import bleach.mcosm.api.ApiDataHandler.Projection;
import io.netty.handler.timeout.ReadTimeoutException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;

public class GuiOSM extends GuiMapBase {
	
	protected ApiDataHandler apiData;
	protected List<JsonObject> ways = new ArrayList<>();
	protected List<JsonObject> nodes = new ArrayList<>();
	
	private GuiTextField latField;
	private GuiTextField lonField;
	private GuiTextField lat1Field;
	private GuiTextField lon1Field;
	
	private GuiTextField outputField;
	
	public GuiOSM(double lat, double lon, double lat1, double lon1) {
		super(lat, lon, lat1, lon1);
	}
	
	public void initGui() {
		super.initGui();
		
		addButton(new GuiButton(0, mapX + mapLen - 75, mapY + mapHei + 5, 75, 20, "Start"));
		addButton(new GuiButton(1, mapX, mapY + mapHei + 5, 75, 20, "Cancel"));
		
		addButton(new GuiButton(2, mapX - 65, mapY, 60, 20, "\u00a7aBuildings"));
		addButton(new GuiButton(3, mapX - 65, mapY + 22, 60, 20, "\u00a7aRoads"));
		addButton(new GuiButton(4, mapX - 65, mapY + 44, 60, 20, "\u00a7aTrees"));
		
		addButton(new GuiButton(5, mapX - 65, mapY + 72, 60, 20, "Global"));
		
		addButton(new GuiButton(6, mapX + mapLen - 153, mapY + mapHei + 5, 75, 20, "Download"));
		
		latField = new GuiTextField(256, fontRenderer, mapX + mapLen + 4, mapY + 10, 80, 20);
		lonField = new GuiTextField(257, fontRenderer, mapX + mapLen + 4, mapY + 45, 80, 20);
		lat1Field = new GuiTextField(258, fontRenderer, mapX + mapLen + 4, mapY + 80, 80, 20);
		lon1Field = new GuiTextField(259, fontRenderer, mapX + mapLen + 4, mapY + 115, 80, 20);
		
		outputField = new GuiTextField(260, fontRenderer, mapX + mapLen + 4, mapY + 155, 80, 20);
		
		latField.setText(lat + "");
		lonField.setText(lon + ""); // who needs toString
		lat1Field.setText(lat1 + "");
		lon1Field.setText(lon1 + "");
		
		buttonList.get(0).enabled = false;
		//outputField.setEnabled(false);
    }
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		drawString(fontRenderer, "Lat 1:", mapX + mapLen + 4, mapY, 0x909090);
		drawString(fontRenderer, "Lon 1:", mapX + mapLen + 4, mapY + 35, 0x909090);
		drawString(fontRenderer, "Lat 2:", mapX + mapLen + 4, mapY + 70, 0x909090);
		drawString(fontRenderer, "Lon 2:", mapX + mapLen + 4, mapY + 105, 0x909090);
		drawString(fontRenderer, "Output:", mapX + mapLen + 4, mapY + 145, 0xc0c0c0);
		
		if (apiData != null) {
			drawString(fontRenderer, "Structures: " + (ways.size() + nodes.size()), mapX + 2, mapY - 10, 0x90a0a0);
		}
		
		latField.drawTextBox();
		lonField.drawTextBox();
		lat1Field.drawTextBox();
		lon1Field.drawTextBox();
		outputField.drawTextBox();
	}
	
	protected void actionPerformed(GuiButton button) throws IOException {
		switch (button.id) {
			case 0:
				if (apiData == null) break;
				
				updateLists();
				apiData.ways = new ArrayList<>(ways);
				apiData.nodes = new ArrayList<>(nodes);
				if (buttonList.get(5).displayString.equals("Local")) apiData.proj = Projection.BTE_PLAYER;
				apiData.addToInstance(McOSM.osmInst);
				Minecraft.getMinecraft().displayGuiScreen(null);
				break;
			case 1:
				Minecraft.getMinecraft().displayGuiScreen(null);
				break;
			case 2: case 3: case 4:
				String s = button.displayString;
				button.displayString = (s.startsWith("\u00a7c") ? "\u00a7a" : "\u00a7c") + s.substring(2);
				updateLists();
				break;
			case 5:
				button.displayString = button.displayString.equals("Global") ? "Local" : "Global";
				break;
			case 6:
				int status = -1;
				try {
					double minLat = Double.parseDouble(latField.getText());
					double minLon = Double.parseDouble(lonField.getText());
					double maxLat = Double.parseDouble(lat1Field.getText());
					double maxLon = Double.parseDouble(lon1Field.getText());
					
					String link = "https://overpass-api.de/api/interpreter?data="
								+ URLEncoder.encode(OSMApiCommand.getApiLink(minLat, minLon, maxLat, maxLon).substring(45), "utf-8");
					URL url = new URL(link);
					System.out.println(url);
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/75.0.1");
					con.connect();
					status = con.getResponseCode();
					if (status < 200 || status > 300) {
						throw new IOException();
					}
					
					BufferedReader outReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String response = "";
					String line;
					while ((line = outReader.readLine()) != null) {
						response += line;
					}
					
					new JsonParser().parse(response); // Validate that is is real json
					
					apiData = new ApiDataHandler(response, Projection.BTE_00);
					this.ways = new ArrayList<>(apiData.ways);
					this.nodes = new ArrayList<>(apiData.nodes);
					buttonList.get(0).enabled = true;
					buttonList.get(6).enabled = false;
					outputField.setText("\u00a7aSuccess! (" + String.format("%,d", response.length()) + " Bytes)");
				} catch (NumberFormatException e) {
					outputField.setText("\u00a7cInvalid Lat/Lon!");
					e.printStackTrace();
				} catch (SocketTimeoutException | ReadTimeoutException e) {
					outputField.setText("\u00a7cInvalid Json (rate limit)");
					e.printStackTrace();
				} catch (IOException e) {
					outputField.setText("\u00a7cIO Exception! (Code " + status + ")");
					e.printStackTrace();
				} catch (NoSuchElementException e) {
					outputField.setText("\u00a7cEmpty Page???");
					e.printStackTrace();
				} catch (JsonParseException e) {
					outputField.setText("\u00a7cInvalid Json (rate limit)");
					e.printStackTrace();
				}
				
				break;
		}
    }
	
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		
		outputField.textboxKeyTyped(typedChar, keyCode);
		
		if (Character.isLetter(typedChar)) return;
		
		latField.textboxKeyTyped(typedChar, keyCode);
		lonField.textboxKeyTyped(typedChar, keyCode);
		lat1Field.textboxKeyTyped(typedChar, keyCode);
		lon1Field.textboxKeyTyped(typedChar, keyCode);
		
		if (latField.isFocused()) try { lat = Double.parseDouble(latField.getText()); } catch (Exception e) { }
		if (lonField.isFocused()) try { lon = Double.parseDouble(lonField.getText()); } catch (Exception e) { }
		if (lat1Field.isFocused()) try { lat1 = Double.parseDouble(lat1Field.getText()); } catch (Exception e) { }
		if (lon1Field.isFocused()) try { lon1 = Double.parseDouble(lon1Field.getText()); } catch (Exception e) { }
    }
	
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		latField.mouseClicked(mouseX, mouseY, mouseButton);
		lonField.mouseClicked(mouseX, mouseY, mouseButton);
		lat1Field.mouseClicked(mouseX, mouseY, mouseButton);
		lon1Field.mouseClicked(mouseX, mouseY, mouseButton);
		outputField.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	protected void updateLists() {
		ways = apiData.ways.stream().filter(j -> 
				(buttonList.get(2).displayString.startsWith("\u00a7a") && j.get("tags").getAsJsonObject().get("building") != null)
				|| (buttonList.get(3).displayString.startsWith("\u00a7a") && j.get("tags").getAsJsonObject().get("highway") != null))
				.collect(Collectors.toList());
		
		nodes = apiData.nodes.stream().filter(j -> buttonList.get(4).displayString.startsWith("\u00a7a")).collect(Collectors.toList());
	}
}
