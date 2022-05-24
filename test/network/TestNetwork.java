package network;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpResponse;

import org.junit.Assert;
import org.junit.Test;

import network.SearchEngine.BadKeywordException;

public class TestNetwork {

	@Test
	public void testConnectionAvailable() throws IOException {
		ConnectionManager.isNetworkAvailable();
	}
	
	@Test
	public void testMakeRequest() throws IOException, InterruptedException {
		URI uri = URI.create("https://www.google.hu");
		HttpResponse<String> response = ConnectionManager.makeRequest(uri);
		Assert.assertEquals(200,response.statusCode());
	}
	
	@Test
	public void testGenerateUriFromKeyword() throws BadKeywordException {
		String keyword = "Batman";
		URI result = SearchEngine.generateUriFromKeyword(keyword);
		URI expected = URI.create("https://v2.sg.media-imdb.com/suggests/b/batman.json");
		Assert.assertEquals(expected,result);
	}
	
	@Test (expected = BadKeywordException.class)
	public void testGenerateUriFromBadKeyword() throws BadKeywordException {
		String keyword = ".-,.-.,.-";
		SearchEngine.generateUriFromKeyword(keyword);
	}
	
	@Test
	public void testGenerateUriFromId() {
		String titleid = "tt7286456";
		URI result = SearchEngine.generateUriFromId(titleid);
		URI expected = URI.create("https://www.imdb.com/title/tt7286456/");
		Assert.assertEquals(expected,result);
	}

}
