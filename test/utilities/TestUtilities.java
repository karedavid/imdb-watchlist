package utilities;

import org.junit.Assert;
import org.junit.Test;

public class TestUtilities {

	@Test
	public void testConversion() {
		String jsonp = "imdb$joker({\"v\":1,\"q\":\"joker\"})";
		String json = JsonProcessor.convertJspToJs(jsonp);
		Assert.assertEquals("{\"v\":1,\"q\":\"joker\"}", json);
	}

}
