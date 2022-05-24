package data;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import utilities.ImageProcessor;

public class Movie implements Serializable{
	
	private static final long serialVersionUID = 1402406618195912852L;
	
	// Fields from JSON
	private String 	title; 	//l
	private String 	id;		//id
	private String 	actor;	//s
	private int 	year;	//y
	private String 	type;	//q
	private String 	media;	//i[0], optional
	
	// Additional data
	private Boolean seen;
	private transient BufferedImage cover;
	
	public Movie(JsonObject jsObject) {
		
		//Checking result type
		
		if(jsObject.get("l") == null) {
			title = "unknown";
		}else{
			title = jsObject.get("l").getAsString();
		}
		
		if(jsObject.get("id") == null) {
			id = null;
		}else{
			id = jsObject.get("id").getAsString();
		}
		
		if(jsObject.get("s") == null) {
			actor = "unknown";
		}else{
			actor = jsObject.get("s").getAsString();
		}
		
		if(jsObject.get("y") == null) {
			year = 0;
		}else{
			year = jsObject.get("y").getAsInt();
		}
		
		if(jsObject.get("q") == null) {
			type = "not specified";
		}else{
			type = jsObject.get("q").getAsString();
		}
		
		//If there's no cover
		if(jsObject.getAsJsonArray("i") == null) {
			media = null;
		}else {
			String[] arrName = new Gson().fromJson(jsObject.getAsJsonArray("i"), String[].class);
			media = arrName[0];
		}
		
		seen = false;
	    
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getId() {
		return id;
	}
	
	public String getActor() {
		return actor;
	}
	
	public int getYear() {
		return year;
	}
	
	public String getType() {
		return type;
	}
	
	public String getMedia() {
		return media;
	}
	
	public void setSeen(Boolean seen) {
		this.seen = seen;
	}

	public void setCover(BufferedImage cover) {
		this.cover = cover;
	}
	
	public BufferedImage getCover() {
		return cover;
	}

	public Boolean getSeen() {
		return seen;
	}
	
	//Download and set cover
	public void autoSetCover() {
		this.setCover(ImageProcessor.resizeImage(ImageProcessor.downloadImage(this.getMedia()), 150, 222));
	}
	
	//Returns a summary of the movie in text format
	public String getSummary() {
		return (getTitle() + " (" + getYear() + ")\n" +
				getActor() + "\n" +
				getType() + "\n" +
				getId());
	}
}
