package bleach.mcosm.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class API {
	
	public static final String API_LINK = "https://overpass-api.de/api/interpreter?data=";

	public static String getApiLink(double minLat, double minLon, double maxLat, double maxLon, boolean encoded) {
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
		String link = "[out:json];(way(" + coords + ");node[natural=tree](" + coords + "););out geom(" + boundCoords + ");";

		try {
			return API_LINK + (encoded ? URLEncoder.encode(link, "utf-8") : link);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String call(URL url) throws IOException {
		URLConnection con = url.openConnection();
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/75.0.1");
		con.setConnectTimeout(15000);
		con.setReadTimeout(15000);
		
		return IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
	}
}
