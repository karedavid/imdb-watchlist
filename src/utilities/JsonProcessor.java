package utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonProcessor {
	
	//Converts JSONP to JSON
	public static String convertJspToJs(String jsonp) { 
		
		if(jsonp.indexOf("(") == -1 || jsonp.lastIndexOf(")") == -1) {
			return null;
		}
		return jsonp.substring(jsonp.indexOf("(") + 1, jsonp.lastIndexOf(")"));
		
		
	}
	
	//Converts JSONP string to JSON Object
	public static JsonObject stringToJson(String jsonpAsString) {
		
		// Convert to a JSONP to JSON
		String json = convertJspToJs(jsonpAsString);
		if(json == null) {
			return null;
		}
		JsonElement root = JsonParser.parseString(json);
		    
		//Convert the input stream to a json element
		JsonObject rootobj = root.getAsJsonObject(); //May be an array, may be an object. 
  
		return rootobj;
	}
	
	public static JsonArray getJsonArray(JsonObject rootObj) {
		return rootObj.getAsJsonArray("d");
	}

}
