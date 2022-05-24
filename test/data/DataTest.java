package data;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import org.junit.Assert;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataTest {

	Movie movie;
	JsonObject js;
	
	@SuppressWarnings("deprecation")
	@Before
	public void initializeTest() {
		
		String responseObject = "{\r\n" + 
				"         \"l\":\"Joker\",\r\n" + 
				"         \"id\":\"tt7286456\",\r\n" + 
				"         \"s\":\"Joaquin Phoenix, Robert De Niro\",\r\n" + 
				"         \"y\":2019,\r\n" + 
				"         \"q\":\"feature\",\r\n" + 
				"         \"vt\":42,\r\n" + 
				"         \"i\":[\r\n" + 
				"            \"https://m.media-amazon.com/images/M/MV5BNGVjNWI4ZGUtNzE0MS00YTJmLWE0ZDctN2ZiYTk2YmI3NTYyXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_.jpg\",\r\n" + 
				"            2764,\r\n" + 
				"            4096\r\n" + 
				"         ],\r\n" + 
				"         \"v\":[\r\n" + 
				"            {\r\n" + 
				"               \"l\":\"Theatrical Trailer\",\r\n" + 
				"               \"id\":\"vi1723318041\",\r\n" + 
				"               \"s\":\"2:25\",\r\n" + 
				"               \"i\":[\r\n" + 
				"                  \"https://m.media-amazon.com/images/M/MV5BOTU0ZGVmY2MtMTM1OS00YmNlLWE1NGUtMGYyMjI1MjY1NWUzXkEyXkFqcGdeQWFybm8@._V1_.jpg\",\r\n" + 
				"                  1404,\r\n" + 
				"                  797\r\n" + 
				"               ]\r\n" + 
				"            },\r\n" + 
				"            {\r\n" + 
				"               \"l\":\"Is 'The Batman' Logo a Deadly Reminder?\",\r\n" + 
				"               \"id\":\"vi1638972953\",\r\n" + 
				"               \"s\":\"3:36\",\r\n" + 
				"               \"i\":[\r\n" + 
				"                  \"https://m.media-amazon.com/images/M/MV5BMjgxMjY3YjAtZDA5ZC00MjMyLWExOGItMjA4NzA4YTgyZGIwXkEyXkFqcGdeQWFsZWxvZw@@._V1_.jpg\",\r\n" + 
				"                  1920,\r\n" + 
				"                  1080\r\n" + 
				"               ]\r\n" + 
				"            },\r\n" + 
				"            {\r\n" + 
				"               \"l\":\"Official Teaser Trailer\",\r\n" + 
				"               \"id\":\"vi2883960089\",\r\n" + 
				"               \"s\":\"2:25\",\r\n" + 
				"               \"i\":[\r\n" + 
				"                  \"https://m.media-amazon.com/images/M/MV5BMGQ1ZGZmNTAtM2MyYi00NmZhLTkwYmYtNTNlZDRhMzU2ZTgwXkEyXkFqcGdeQW1yb3NzZXI@._V1_.jpg\",\r\n" + 
				"                  1920,\r\n" + 
				"                  1080\r\n" + 
				"               ]\r\n" + 
				"            }\r\n" + 
				"         ]\r\n" + 
				"      }";
		
		js = new JsonParser().parse(responseObject).getAsJsonObject();
		
	}
	
	@Test
	public void testMovieConstructor() {
		
		movie = new Movie(js);
		
		Assert.assertEquals("Joker", movie.getTitle());
		Assert.assertEquals("tt7286456", movie.getId());
		Assert.assertEquals("Joaquin Phoenix, Robert De Niro", movie.getActor());
		Assert.assertEquals(2019, movie.getYear());
		Assert.assertEquals("feature", movie.getType());
		Assert.assertEquals("https://m.media-amazon.com/images/M/MV5BNGVjNWI4ZGUtNzE0MS00YTJmLWE0ZDctN2ZiYTk2YmI3NTYyXkEyXkFqcGdeQXVyMTkxNjUyNQ@@._V1_.jpg", movie.getMedia());
		Assert.assertEquals(false, movie.getSeen());
	}
	
	@Test
	public void testSummary() {
		movie = new Movie(js);
		Assert.assertEquals("Joker (2019)\nJoaquin Phoenix, Robert De Niro\nfeature\ntt7286456", movie.getSummary());
	}
	
	@Test
	public void testAddToDatabase() {
		MovieCollectionLocal db = new MovieCollectionLocal();
		db.addMovieBeginning(movie = new Movie(js));
		Assert.assertEquals(1, db.getSize());
		Assert.assertEquals(movie, db.getMovie(0));
	}
	
	@Test
	public void testRemoveFromDatabase() {
		MovieCollectionLocal db = new MovieCollectionLocal();
		db.addMovieBeginning(movie = new Movie(js));
		db.removeMovie(movie);
		Assert.assertEquals(0, db.getSize());
	}
	
	@Test
	public void testAddDuplicate() {
		MovieCollectionLocal db = new MovieCollectionLocal();
		db.addMovieBeginning(movie = new Movie(js));
		db.addMovieBeginning(movie = new Movie(js));
		Assert.assertEquals(1, db.getSize());
	}
	
	
	@Test
	public void testSerializeDatabase() throws IOException {
		MovieCollectionLocal db = new MovieCollectionLocal();
		db.addMovieBeginning(movie = new Movie(js));
		db.serializeDatabase();
	}
	
	@Test
	public void testDeserializeDatabase() throws IOException, ClassNotFoundException {
		MovieCollectionLocal db1 = new MovieCollectionLocal();
		db1.addMovieBeginning(movie = new Movie(js));
		db1.serializeDatabase();
		
		MovieCollectionLocal db2 = new MovieCollectionLocal();
		db2.deserializeDatabase();
		Assert.assertEquals(1, db2.getSize());
	}
	

	

}
