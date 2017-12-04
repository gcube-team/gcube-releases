package org.gcube.social_networking.socialutillibrary;

import java.util.List;


public class TestUnit {

	//@Test
	public void testHashtag() {
		String text = "This is a test with hashtag #T6 and #T6.1 but also #T6. that has '.' that is useless and #T43.43 and #gcube4.1.0gcore #gcube4.1.0 and (#ga) tewrw and https://www.local.it/#here";
		List<String> hashtags = Utils.getHashTags(text);
		System.out.println("Hashtags are " + hashtags);
	}

	//@Test
	public void extractUrl(){

		String url = "https://virtuoso.parthenos.d4science.org/sparql?default-graph-uri=&query=%09SELECT+%3Fp+%28COUNT%28%3Fp%29+as+%3FpCount%29++%0D%0A%09%09%09%09%09WHERE+%7B%5B%5D+%3Fp+%5B%5D%7D%0D%0A%09%09%09%09%09GROUP+BY+%3Fp&format=text%2Fhtml&timeout=0&debug=on";
		String result = Utils.extractURL(url);
		System.out.println("urls are " + result);
	}


}
