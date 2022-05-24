package network;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import data.Movie;
import data.MovieCollectionSearch;
import utilities.JsonProcessor;

public class SearchEngine { 
	
	public static class NoResultException extends Exception{
		private static final long serialVersionUID = -7336593010373706749L;
	}
	
	public static class BadKeywordException extends Exception{
		private static final long serialVersionUID = -6289759728793951541L;
	}
	
	 //Temporarily store search results
	private static MovieCollectionSearch searchResults = null;
	
	///Make a search with keyword
	public static void search(String keyword) throws NoResultException, IOException, InterruptedException, BadKeywordException {
		
		JsonArray results = null;
		
		URI uri;
		try {
			uri = generateUriFromKeyword(keyword);
		} catch (BadKeywordException e) {
			throw e;
		}
		HttpResponse<String> response;
		response = ConnectionManager.makeRequest(uri);
		JsonObject json = JsonProcessor.stringToJson(response.body());
		if(json == null) {
			throw new BadKeywordException();
		}
		results = json.getAsJsonArray("d");
		
		//If there are results, move them to temporary storage
		if(results != null) {
			searchResults = new MovieCollectionSearch();
			for(JsonElement i : results) {
				if(i.getAsJsonObject().get("id").getAsString().startsWith("tt")) {
					searchResults.addMovieEnd(new Movie(i.getAsJsonObject()));
				}	
			}
			
			if(searchResults.getSize() == 0) {
				throw new NoResultException();
			}
		}else {
			throw new NoResultException();
		}
	}
	
	public static MovieCollectionSearch getResults() {
		return searchResults;
	}
	
	public static URI generateUriFromKeyword(String keyword) throws BadKeywordException {
		
		String urlAsString = "https://v2.sg.media-imdb.com/suggests/";
		keyword = keyword.replaceAll("[öÖőŐóÓ]", "o");
		keyword = keyword.replaceAll("[üÜúÚűŰ]", "u");
		keyword = keyword.replaceAll("[áÁ]", "a");
		keyword = keyword.replaceAll("[éÉ]", "e");
		keyword = keyword.trim().replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
		
		//If filtered keyword is empty
		if(keyword.length() == 0) {
			throw new BadKeywordException();
		}
		
		urlAsString = urlAsString.concat(keyword.charAt(0) + "/" + keyword + ".json");
		urlAsString = urlAsString.replaceAll(" ", "%20");
		URI uri = URI.create(urlAsString);
		return uri;
		
	}

	public static URI generateUriFromId(String title) {
		
		String urlAsString = "https://www.imdb.com/title/";
		urlAsString = urlAsString.concat(title + "/");
		URI uri = URI.create(urlAsString);
		return uri;
		
	}
}
