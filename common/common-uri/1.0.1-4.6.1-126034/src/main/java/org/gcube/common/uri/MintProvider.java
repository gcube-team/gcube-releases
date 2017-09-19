package org.gcube.common.uri;

/**
 * Configurable factory of {@link Mint} instances.
 * <p>
 * It can be used as the simplest approach to enable client code testing.
 * 
 * @author Fabio Simeoni
 *
 */
public class MintProvider {

	private volatile static Mint mint = new ScopedMint();
	
	/**
	 * Provides a {@link Mint}
	 * @return the mint
	 */
	public static Mint mint() {
		return mint;
	}
	
	/**
	 * Sets the {@link Mint} to provide.
	 * @param mint the mint
	 */
	public static void setMint(Mint mint) {
		MintProvider.mint = mint;
	}
	
	
}
