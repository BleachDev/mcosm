package bleach.mcosm.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import bleach.mcosm.McOSM;
import org.apache.commons.io.IOUtils;

import javax.net.ssl.*;

public class API {
	// https://wiki.openstreetmap.org/wiki/Overpass_API
	public static final String[] API_ENDPOINTS = {
			"https://overpass-api.de/api/interpreter",
			"https://maps.mail.ru/osm/tools/overpass/api/interpreter",
			"https://overpass.private.coffee/api/interpreter"
	};

	private static SSLSocketFactory sslSocketFactory;

	public static void init() throws Exception {
		KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
		keyStore.load(null, null);
		
		// Load default cacerts entries into keystore first, then add the bundled cert
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		try (InputStream stream = API.class.getClassLoader().getResourceAsStream("assets/bleach/cert/isrgrootx1.pem")) {
			Certificate cert = certificateFactory.generateCertificate(stream);
			keyStore.setCertificateEntry("isrgrootx1", cert);
		}
		
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
		
		sslSocketFactory = sslContext.getSocketFactory();
	}

	public static String getApiQuery(double minLat, double minLon, double maxLat, double maxLon) {
		// Encoding is only needed internally for API calls, external calls should be unencoded
		return getApiQuery(minLat, minLon, maxLat, maxLon, false, false);
	}
	
	private static String getApiQuery(double minLat, double minLon, double maxLat, double maxLon, boolean encoded, boolean buildingsOnly) {
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
		String link;
		if (!buildingsOnly) {
			String boundCoords = (minLat - 0.0002) + "," + (minLon - 0.0002) + "," + (maxLat + 0.0002) + "," + (maxLon + 0.0002);
			link = "[out:json];(way(" + coords + ");node[natural=tree](" + coords + "););out geom(" + boundCoords + ");";
		} else {
			link = "[out:json];(way[building](" + coords + "););out geom;";
		}

		try {
			return "?data=" + (encoded ? URLEncoder.encode(link, "utf-8") : link);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getBuildingData(double playerLat, double playerLon) {
		return getOsmData(playerLat - 0.001, playerLon - 0.001, playerLat + 0.001, playerLon + 0.001, true);
	}

	public static String getOsmData(double minLat, double minLon, double maxLat, double maxLon) throws RuntimeException {
		return getOsmData(minLat, minLon, maxLat, maxLon, false);
	}

	public static String getOsmData(double minLat, double minLon, double maxLat, double maxLon, boolean buildingsOnly) throws RuntimeException {
		for (String endpoint : API_ENDPOINTS) {
			String url = endpoint + getApiQuery(minLat, minLon, maxLat, maxLon, true, buildingsOnly);
			try {
				return call(new URL(url));
			} catch (IOException e) {
				McOSM.LOGGER.error("Host {}: {}", endpoint, e);
				McOSM.LOGGER.error("Full URL: {}", url);
			}
		}

		// If we reach this point, all endpoints have failed
		throw new RuntimeException(String.format("Failed to get OSM data after %s tries, see log for more details", API_ENDPOINTS.length));
	}
	
	private static String call(URL url) throws IOException {
		URLConnection con = openConnection(url);
		con.setRequestProperty("User-Agent", String.format("%s %s (https://github.com/BleachDev/mcosm)", McOSM.NAME, McOSM.VERSION));
		con.setConnectTimeout(15 * 1000);
		// Larger regions make take longer to be loaded by the server and consequently to download by the client
		con.setReadTimeout(5 * 60 * 1000);

		return IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
	}

	private static URLConnection openConnection(URL url) throws IOException {
		URLConnection con = url.openConnection();

		if (con instanceof HttpsURLConnection) {
			// Hopefully fix SSL errors for several years
			HttpsURLConnection httpsCon = (HttpsURLConnection) con;
			httpsCon.setSSLSocketFactory(sslSocketFactory);

			return httpsCon;
		}

		return con;
	}
}
