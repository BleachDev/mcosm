package bleach.mcosm.api;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import bleach.mcosm.OSMInstance;
import bleach.mcosm.struct.BuildingStruct;
import bleach.mcosm.struct.HouseStruct;
import bleach.mcosm.struct.RoadStruct;
import bleach.mcosm.struct.TreeStruct;
import bleach.mcosm.utils.GeoPos;
import net.minecraft.block.BlockConcretePowder;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockStainedHardenedClay;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import scala.Tuple2;

public class ApiDataHandler {

	private String data;
	private Projection proj;
	
	private List<JsonObject> ways = new ArrayList<>();
	private List<JsonObject> nodes = new ArrayList<>();
	
	private double minLat = Integer.MAX_VALUE, minLon = Integer.MAX_VALUE, maxLat = Integer.MIN_VALUE, maxLon = Integer.MIN_VALUE;
	
	public ApiDataHandler(String json, Projection proj) {
		this.data = json;
		this.proj = proj;
		
		PriorityQueue<Tuple2<Integer /* priority */, JsonObject>> tempWays = new PriorityQueue<Tuple2<Integer, JsonObject>>(
				new Comparator<Tuple2<Integer, JsonObject>>() {
					public int compare(Tuple2<Integer, JsonObject> o1, Tuple2<Integer, JsonObject> o2) {
						return o1._1 > o2._1 ? -1 : o1._1 == o2._1 ? 0 : 1;
					}
				});
		
		JsonObject jsondata = new JsonParser().parse(data).getAsJsonObject();
		for (JsonElement j: jsondata.get("elements").getAsJsonArray()) {
			JsonObject jobj = j.getAsJsonObject();
			
			try {
				JsonObject jbounds = jobj.get("bounds").getAsJsonObject();
				minLat = Math.min(minLat, jbounds.get("minlat").getAsDouble());
				minLon = Math.min(minLon, jbounds.get("minlon").getAsDouble());
				maxLat = Math.max(maxLat, jbounds.get("maxlat").getAsDouble());
				maxLon = Math.max(maxLon, jbounds.get("maxlon").getAsDouble());
			} catch (Exception e) { }
			
			switch (jobj.get("type").getAsString()) {
				case "node":
					nodes.add(jobj);
					break;
				case "way":
					JsonElement jtags = jobj.get("tags");
					if (jtags != null) {
						if (jtags.getAsJsonObject().get("building") != null) {
							
							tempWays.add(new Tuple2<Integer, JsonObject>(3, jobj));
							
						} else if (jtags.getAsJsonObject().get("highway") != null) {
							
							if (jtags.getAsJsonObject().get("highway").getAsString().equals("service")) tempWays.add(new Tuple2<Integer, JsonObject>(2, jobj));
							else if (jtags.getAsJsonObject().get("highway").getAsString().equals("cycleway"))  tempWays.add(new Tuple2<Integer, JsonObject>(1, jobj));
							else tempWays.add(new Tuple2<Integer, JsonObject>(0, jobj));
							
						}
					}
					
					break;
			}
		}
		
		tempWays.forEach(v -> ways.add(v._2));
	}
	
	public void addToInstance(OSMInstance inst) {
		for (JsonObject j: nodes) {
			double lat = j.get("lat").getAsDouble();
			double lon = j.get("lon").getAsDouble();
			
			if (j.get("tags") != null) {
				JsonObject jtags = j.get("tags").getAsJsonObject();
				
				if (jtags.get("natural").getAsString().equals("tree")) inst.add(
						new TreeStruct(latLonToPos(lat, lon, minLat + (maxLat - minLat) / 2, minLon + (maxLon - minLon) / 2)));
			}
		}
		
		for (JsonObject j: ways) {
			List<BlockPos> nodes = new ArrayList<>();
			
			JsonElement jgeom = j.get("geometry");
			if (jgeom != null) {
				for (JsonElement jg: jgeom.getAsJsonArray()) {
					if (jg.isJsonNull()) continue;
					
					JsonObject jgobj = jg.getAsJsonObject();
					
					nodes.add(latLonToPos(jgobj.get("lat").getAsDouble(), jgobj.get("lon").getAsDouble(),
							minLat + (maxLat - minLat) / 2, minLon + (maxLon - minLon) / 2));
				}
			}
			
			if (j.get("tags") != null) {
				JsonObject jtags = j.get("tags").getAsJsonObject();
				
				JsonElement jbuilding = jtags.get("building");
				if (jbuilding != null) {
					IBlockState blockType = Blocks.CONCRETE.getDefaultState();
					IBlockState windowType = Blocks.CONCRETE.getDefaultState().withProperty(BlockConcretePowder.COLOR, EnumDyeColor.SILVER);
					
					boolean heightSet = false;
					int height = 7;
					int floors = 0;
					
					JsonElement jheight = jtags.get("height");
					JsonElement jfloors = jtags.get("building:levels");
					
					if (jheight != null) {
						height = (int) jheight.getAsDouble();
						heightSet = true;
					}
					
					if (jfloors != null) {
						if (!heightSet) height = (int) Math.round(jfloors.getAsInt() * 3.5);
						heightSet = true;
						floors = jfloors.getAsInt();
					}
					
					if (jbuilding.getAsString().equals("garage")) {
						blockType = Blocks.CONCRETE.getDefaultState().withProperty(BlockConcretePowder.COLOR, EnumDyeColor.GRAY);
						if (!heightSet) height = 4;
						
						inst.add(new BuildingStruct(nodes, blockType, height));
					} else {
						inst.add(new HouseStruct(nodes, blockType, blockType, windowType, height, floors));
					}
				}
				
				JsonElement jroad = jtags.get("highway");
				if (jroad != null) {
					nodes = nodes.stream().map(b -> b.down()).collect(Collectors.toList());
					
					switch (jroad.getAsString()) {
						case "seconday":
							inst.add(new RoadStruct(nodes,
									Blocks.CONCRETE.getDefaultState().withProperty(BlockConcretePowder.COLOR, EnumDyeColor.GRAY), 4));
							break;
						case "trunk":
							inst.add(new RoadStruct(nodes,
									Blocks.CONCRETE.getDefaultState().withProperty(BlockConcretePowder.COLOR, EnumDyeColor.GRAY), 6,
									Blocks.CONCRETE.getDefaultState().withProperty(BlockConcretePowder.COLOR, EnumDyeColor.YELLOW), 5));
							break;
						case "service":
							inst.add(new RoadStruct(nodes, Blocks.GRAVEL.getDefaultState(), 2));
							break;
						case "cycleway":
							inst.add(new RoadStruct(nodes,
									Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockStainedHardenedClay.COLOR, EnumDyeColor.CYAN), 1));
							break;
						case "track":
							inst.add(new RoadStruct(nodes, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT), 2));
							break;
						case "residential":
							inst.add(new RoadStruct(nodes, Blocks.CONCRETE.getDefaultState().withProperty(BlockConcretePowder.COLOR, EnumDyeColor.GRAY), 2));
							break;
						default:
							inst.add(new RoadStruct(nodes,
									Blocks.CONCRETE.getDefaultState().withProperty(BlockConcretePowder.COLOR, EnumDyeColor.GRAY), 5,
									Blocks.CONCRETE.getDefaultState(), 4));
					}
				}
			}
		}
	}
	
	private BlockPos latLonToPos(double lat, double lon, double lat0, double lon0) {
		switch (proj) {
			case NAIVE_00:
				return GeoPos.from00Naive(lat, lon, (int) Minecraft.getMinecraft().player.posY);
			case NAIVE_PLAYER:
				return GeoPos.fromPlayerNaive(lat, lon, lat0, lon0);
			case BTE_00:
				return GeoPos.from00BTE(lat, lon, (int) Minecraft.getMinecraft().player.posY);
			case BTE_PLAYER:
				return GeoPos.fromPlayerBTE(lat, lon, lat0, lon0);
			default:
				System.err.println("Unknown Projection Found!");
				return BlockPos.ORIGIN;
		}
	}
	
	public enum Projection {
		NAIVE_00,
		NAIVE_PLAYER,
		BTE_00,
		BTE_PLAYER
	}
}
