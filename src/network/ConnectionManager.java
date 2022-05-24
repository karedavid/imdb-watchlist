package network;

import java.awt.Desktop;
import java.io.*;
import java.net.*;
import java.net.http.*;

public class ConnectionManager {
	
	//Make a  request
	public static HttpResponse<String> makeRequest(URI uri) throws IOException, InterruptedException {
		
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
			    .uri(uri)
			    .build();
		
		return client.send(request, HttpResponse.BodyHandlers.ofString());
	}

	///Checks if connection is available
	public static void isNetworkAvailable() throws IOException {
	        final URL url = new URL("http://www.imdb.com");
	        final URLConnection conn = url.openConnection();
	        conn.connect();
	        conn.getInputStream().close();
	}
	
	//Open link in web browser
	public static void openLink(URI uri) throws Exception {
		 Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		    if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
		        desktop.browse(uri);
		    }else {
		    	throw new Exception();
		    }
	}

}
