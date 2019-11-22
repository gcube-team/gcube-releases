package org.gcube.smartgears.provider;

public class ProviderFactory {

	private static Provider provider = new DefaultProvider();
	
	public static Provider provider() {
		return provider;
	}
	
	public static void testProvider(Provider provider) {
		ProviderFactory.provider=provider;
	}
}
