package org.gcube.datacatalogue.ckanutillibrary.shared.ex;

/**
 * Thrown when there are more than one application profile, but none of them was set as master
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class NoApplicationProfileMasterException extends Exception {

	private static final long serialVersionUID = 5874713540422734005L;
	private static final String DEFAULT_MESSAGE = "There is more than one application profile into this scope"
			+ " but none of them is set as master!";

	public NoApplicationProfileMasterException(){
		super(DEFAULT_MESSAGE);
	}

	public NoApplicationProfileMasterException(String message) {
		super(message);
	}
}
