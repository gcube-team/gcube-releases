package org.gcube.social_networking.socialutillibrary;

import java.util.List;


public class TestUnit {

	//@Test
	public void testHashtag() {
		String text = "This is a test with hashtag #T6 and #T6.1 but also #T6. that has '.' that is useless and #T43.43 and #gcube4.1.0gcore #gcube4.1.0 and";
		List<String> hashtags = Utils.getHashTags(text);
		System.out.println("Hashtags are " + hashtags);
	}

	//	@Test
	public void extractUrl(){

		String url = "http tosajndjsa :httphttps://www.google.tv www.google.cloud  www https http (http://digirolamo.com: www.google.it https://next.d4science.org/group/nextnext/data-catalogue?path=/dataset/test_for_visibility";
		String result = Utils.transformUrls(url);
		System.out.println("urls are " + result);
	}


}
